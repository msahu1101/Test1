package com.mgmresorts.loyalty.common.specification;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Named;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.mgmresorts.ObjectReference;
import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.errors.ErrorManager.AppError;
import com.mgmresorts.common.errors.ErrorManager.IError;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.openapi.DocParams;
import com.mgmresorts.common.openapi.DocResult;
import com.mgmresorts.common.openapi.WebHook;
import com.mgmresorts.common.openapi.DocParams.Param;
import com.mgmresorts.common.openapi.DocParams.Type;
import com.mgmresorts.common.errors.HttpStatus;
import com.mgmresorts.common.security.ClassScanner;
import com.mgmresorts.common.security.RoleMapping;
import com.mgmresorts.common.security.role.MGMRole;
import com.mgmresorts.common.security.role.RoleAuthorizer;
import com.mgmresorts.common.security.role.ServiceRole;
import com.mgmresorts.common.security.scope.Role;
import com.mgmresorts.common.security.scope.ScopeAuthorizer;
import com.mgmresorts.common.specification.BaseSpecification;
import com.mgmresorts.common.utils.Utils;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.parameters.Parameter.StyleEnum;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import com.mgmresorts.common.config.Runtime;

@Named
@SuppressWarnings("rawtypes")
public final class ApimSpecification extends BaseSpecification {

    @Override
    public OpenAPI generate(String host, String name, boolean webHooksOnly, String defaultAccessKey, String specificationType, String frontDoorHost) throws Exception {
        final OpenAPI api = baseApi(host, name, frontDoorHost);
        api.setSpecVersion(SpecVersion.V30);
        api.setPaths(new Paths());
        final Class<?>[] classes = ClassScanner.findClasses(ObjectReference.class, ClassScanner.isAFunctionClass());
        for (Class<?> clazz : classes) {
            final Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (webHooksOnly) {
                    final WebHook webhook = method.getAnnotation(WebHook.class);
                    if (webhook == null) {
                        continue;
                    }
                }
                final FunctionName annotation = method.getAnnotation(FunctionName.class);
                final Doc docs = method.getAnnotation(Doc.class);
                final DocResult result = method.getAnnotation(DocResult.class);
                final DocParams params = method.getAnnotation(DocParams.class);
                if (docs != null && docs.hidden()) {
                    continue;
                }
                final Map<String, Param> pathParameters = new HashMap<>();
                final Map<String, Param> queryParameters = new HashMap<>();
                if (params != null) {
                    Arrays.asList(params.value()).forEach(a -> {
                        if (a.type() == Type.Path) {
                            pathParameters.put(a.name(), a);
                        } else {
                            queryParameters.put(a.name(), a);
                        }
                    });
                }

                if (annotation != null) {
                    final String value = annotation.value();
                    final Parameter[] parameters = method.getParameters();
                    final Parameter triggerParam = Arrays.asList(parameters).stream().filter(p -> p.isAnnotationPresent(HttpTrigger.class)).findFirst().orElseGet(() -> null);
                    final HttpTrigger trigger = triggerParam != null && triggerParam.getType().isAssignableFrom(HttpRequestMessage.class)
                            ? triggerParam.getAnnotation(HttpTrigger.class)
                            : null;
                    if (trigger != null) {
                        final HttpMethod[] httpMethods = trigger.methods();
                        for (HttpMethod httpMethod : httpMethods) {
                            final String route = trigger.route();
                            final String key = "/" + route + "/";
                            PathItem path = null;
                            if (api.getPaths().containsKey(key)) {
                                path = api.getPaths().get(key);
                            } else {
                                path = new PathItem();
                                api.getPaths().put(key, path);
                            }

                            final Operation operation = new Operation();
                            operation.setTags(Arrays.asList(method.getName() + httpMethod.name().toUpperCase()));
                            operation.setSummary(httpMethod + " " + value);
                            operation.setDescription(docs != null ? docs.readme() : "");
                            operation.setOperationId(method.getName() + "In" + clazz.getSimpleName() + "Using" + httpMethod.name().toUpperCase());
                            securityRequirement(api.getComponents(), operation, method, key);
                            final List<io.swagger.v3.oas.models.parameters.Parameter> swaggerParams = new ArrayList<>();
                            operation.setParameters(swaggerParams);

                            Arrays.asList(parameters).stream().forEach(p -> {
                                if (httpMethod != HttpMethod.GET && p.getType().isAssignableFrom(HttpRequestMessage.class)) {
                                    final ParameterizedType type = (ParameterizedType) p.getParameterizedType();
                                    final ParameterizedType pType = (ParameterizedType) type.getActualTypeArguments()[0];
                                    final Class<?> arg = (Class<?>) pType.getActualTypeArguments()[0];
                                    final Class<?> request = docs != null ? docs.request() : arg;
                                    final RequestBody e = new RequestBody();
                                    e.setDescription("request");
                                    e.setRequired(true);
                                    final String simpleName = simpleName(request);
                                    final Schema<?> schema = schema(request);
                                    api.getComponents().addSchemas(simpleName, schema);
                                    final Content content = new Content();
                                    final MediaType item = new MediaType();
                                    final Schema jsonSchema = new Schema();
                                    jsonSchema.set$ref("#/components/schemas/" + simpleName);
                                    jsonSchema.setTypes(null);
                                    item.setSchema(jsonSchema);
                                    content.addMediaType("application/json", item);
                                    e.setContent(content);

                                    operation.setRequestBody(e);
                                }

                                if (p.isAnnotationPresent(BindingName.class)) {
                                    final BindingName binding = p.getAnnotation(BindingName.class);
                                    final PathParameter qp = new PathParameter();
                                    final String bindingName = binding.value();
                                    qp.setStyle(StyleEnum.SIMPLE);
                                    final Param param = pathParameters.get(bindingName);
                                    final StringSchema schema = new StringSchema();
                                    schema.setName(bindingName);
                                    schema.setDefault(param != null ? param.defaultValue() : null);
                                    schema._enum(param != null ? Arrays.asList(param.enumValues()) : null);
                                    schema.setTypes(null);
                                    qp.setSchema(schema);
                                    qp.setName(bindingName);
                                    qp.setRequired(true);
                                    qp.setDescription(param != null ? param.description() : bindingName);
                                    swaggerParams.add(qp);
                                }

                            });
                            if (params != null) {
                                Arrays.asList(params.value()).forEach(a -> {
                                    if (a.type() != Type.Path) {
                                        final Param param = pathParameters.get(a.name());
                                        final QueryParameter qp = new QueryParameter();
                                        qp.setName(a.name());
                                        qp.setDescription(a.description());
                                        qp.setRequired(a.required());
                                        final StringSchema schema = new StringSchema();
                                        schema.setName(a.name());
                                        schema.setDefault(param != null ? param.defaultValue() : null);
                                        schema._enum(param != null ? Arrays.asList(param.enumValues()) : null);
                                        schema.setTypes(null);
                                        qp.setSchema(schema);
                                        swaggerParams.add(qp);
                                    }
                                });
                            }
                            final ApiResponses responses = new ApiResponses();
                            final Class<?> response = docs != null ? docs.response() : method.getReturnType();
                            responses.put(String.valueOf(HttpStatus.OK.value()), apiResponse(api, response, "Success", true));

                            if (result != null) {
                                for (int code : result.value()) {
                                    AppError appError = ErrorManager.get(code);
                                    if (appError == null) {
                                        /*
                                         * most probably error manager is not initialized
                                         */
                                        final Class<?>[] findClasses = ClassScanner.findClasses(ObjectReference.class, (s) -> IError.class.isAssignableFrom(s));
                                        for (Class<?> claxx : findClasses) {
                                            if (!Modifier.isAbstract(claxx.getModifiers()) && !Modifier.isAbstract(claxx.getModifiers())) {
                                                claxx.newInstance();
                                            }
                                        }
                                        appError = ErrorManager.get(code);

                                    }
                                    final int httpStatus = appError.getHttpStatus().value();
                                    if (!responses.containsKey(String.valueOf(httpStatus))) {
                                        responses.put(String.valueOf(httpStatus), apiResponse(api, response, appError.getMessage(), (httpStatus > 199 && httpStatus < 300)));
                                    }
                                }
                            }
                            operation.setResponses(responses);

                            if (httpMethod == HttpMethod.GET) {
                                path.setGet(operation);
                            } else if (httpMethod == HttpMethod.HEAD) {
                                path.setHead(operation);
                            } else if (httpMethod == HttpMethod.POST) {
                                path.setPost(operation);
                            } else if (httpMethod == HttpMethod.PUT) {
                                path.setPut(operation);
                            } else if (httpMethod == HttpMethod.DELETE) {
                                path.setDelete(operation);
                            } else if (httpMethod == HttpMethod.OPTIONS) {
                                path.setOptions(operation);
                            } else {
                                throw new Exception("Unsupported http method type: " + httpMethod);
                            }

                        }

                    }

                }
            }
        }
        return api;
    }

    protected Schema<?> schema(Class<?> clazz) {
        final JavaType javaType = jsonMapper.getTypeFactory().constructType(clazz);
        if (javaType.isArrayType()) {
            final ArraySchema model = new ArraySchema();
            final Class<?> componentType = clazz.getComponentType();
            model.set$ref("#/components/schemas/" + simpleName(componentType));
            return model;
        } else {
            final ObjectSchema model = new ObjectSchema();
            model.setTypes(null);
            final BeanDescription beanDescription = jsonMapper.getSerializationConfig().introspect(javaType);
            final List<BeanPropertyDefinition> definitions = beanDescription.findProperties();
            model.name(clazz.getSimpleName());
            final Map<String, Schema> properties = new HashMap<>();
            for (BeanPropertyDefinition definition : definitions) {
                final Schema buildProperty = schema(definition);
                if (buildProperty == null) {
                    continue;
                }
                properties.put(definition.getName(), buildProperty);
            }
            model.setProperties(properties);
            return model;
        }

    }

    private Schema<?> schema(BeanPropertyDefinition definition) {
        final Class<?> type = definition.getField() != null ? definition.getField().getRawType() : definition.getGetter().getRawReturnType();
        Schema<?> property = null;
        if (type != Class.class) {
            property = schema(type, definition.getField() != null ? definition.getField().getAnnotated() : null);
            if (property != null) {
                property.setName(definition.getName());
                property.setTitle(definition.getInternalName());
                final String description = (definition.getField() != null && definition.getField().hasAnnotation(JsonPropertyDescription.class))
                        ? definition.getField().getAnnotation(JsonPropertyDescription.class).value()
                        : definition.getName();
                property.setDescription(description);
                property.setTitle(description);
                property.setRequired(Arrays.asList(definition.getName()));
                property.setTypes(null);
            }
        }
        return property;

    }

    private Schema<?> schema(final Class<?> type, Field annotated) {
        Schema<?> property = null;
        if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
            property = new IntegerSchema();
        } else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
            property = new NumberSchema();
        } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
            property = new NumberSchema();
        } else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
            property = new NumberSchema();
        } else if (type.isArray()) {
            final ArraySchema objectProperty = new ArraySchema();
            if (annotated != null) {
                final Class<?> argument = type.getComponentType();
                objectProperty.setItems(schema(argument, annotated));
                property = objectProperty;
            }
            property = objectProperty;
        } else if (type.isAssignableFrom(Set.class) || type.isAssignableFrom(List.class)) {
            final ArraySchema objectProperty = new ArraySchema();
            if (annotated != null) {
                final ParameterizedType genericType = (ParameterizedType) annotated.getGenericType();
                final Class<?> argument = (Class<?>) genericType.getActualTypeArguments()[0];
                objectProperty.setItems(schema(argument, annotated));
                property = objectProperty;
            }
        } else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
            property = new BooleanSchema();
        } else if (type.isAssignableFrom(Date.class)) {
            property = new DateSchema();
        } else if (type.isAssignableFrom(LocalDate.class)) {
            property = new DateSchema();
            ((DateSchema) property).setFormat(ISO_LOCAL_DATE);
        } else if (type.isAssignableFrom(LocalDateTime.class)) {
            property = new DateTimeSchema();
            ((DateTimeSchema) property).setFormat(ISO_ZONED_DATE_TIME);
        } else if (type.isAssignableFrom(ZonedDateTime.class)) {
            property = new DateTimeSchema();
            ((DateTimeSchema) property).setFormat(ISO_ZONED_DATE_TIME);
        } else if (type.isAssignableFrom(Map.class)) {
            property = new MapSchema();
        } else if (type.isAssignableFrom(String.class)) {
            property = new StringSchema();
        } else if (type.isAssignableFrom(UUID.class)) {
            property = new UUIDSchema();
        } else if (type.isEnum()) {
            final StringSchema stringProperty = new StringSchema();
            final Object[] enumConstants = type.getEnumConstants();
            final List<String> enums = new ArrayList<>();
            for (Object object : enumConstants) {
                enums.add(String.valueOf(object));
            }
            stringProperty.setEnum(enums);
            property = stringProperty;

        } else {
            final ObjectSchema objectProperty = new ObjectSchema();
            final JavaType javaType = jsonMapper.getTypeFactory().constructType(type);
            final BeanDescription beanDescription = jsonMapper.getSerializationConfig().introspect(javaType);
            final List<BeanPropertyDefinition> definitions = beanDescription.findProperties();
            for (BeanPropertyDefinition beanPropertyDefinition : definitions) {
                final Schema<?> buildProperty = schema(beanPropertyDefinition);
                if (buildProperty == null) {
                    continue;
                }
                if (objectProperty.getProperties() == null) {
                    objectProperty.setProperties(new HashMap<String, Schema>());
                }
                objectProperty.getProperties().put(beanPropertyDefinition.getName(), buildProperty);

            }
            property = objectProperty;
        }
        property.setTypes(null);
        return property;
    }




    private void securityRequirement(Components components, Operation operation, Method method, String key) {
        final String name = method.getAnnotation(FunctionName.class).value();
        if (name != null) {
            final SecurityRequirement scopesRequirement = scopeSecurityRequirement(name);
            final SecurityRequirement mgmRoleRequirement = mgmRoleSecurityRequirements(name);
            final SecurityRequirement serviceRoleRequirement = serviceRoleSecurityRequirements(name);
            if (scopesRequirement != null) {
                final Scopes scopes = new Scopes();
                for (Role role : ScopeAuthorizer.get().getScopes()) {
                    scopes.addString(role.getRoleName(), role.getDescription());
                }
                components.addSecuritySchemes(SCHEME_SECURITY_NAME, securityScheme(scopes));
                operation.addSecurityItem(scopesRequirement);
            }
            if (mgmRoleRequirement != null) {
                final List<String> list = mgmRoleRequirement.get(SCHEME_MGM_ROLE);
                final Scopes scopes = new Scopes();
                for (String role : list) {
                    scopes.addString(role, MGMRole.Role.descriptionOf(role));
                }
                components.addSecuritySchemes(SCHEME_MGM_ROLE, securityScheme(scopes));
                operation.addSecurityItem(mgmRoleRequirement);
            }
            if (serviceRoleRequirement != null) {
                final List<String> list = serviceRoleRequirement.get(SCHEME_SERVICE_ROLE);
                final Scopes scopes = new Scopes();
                for (String role : list) {
                    scopes.addString(role, ServiceRole.Role.descriptionOf(role));
                }
                components.addSecuritySchemes(SCHEME_SERVICE_ROLE, securityScheme(scopes));
                operation.addSecurityItem(serviceRoleRequirement);
            }
        }
    }

    protected SecurityScheme securityScheme(Scopes scopes) {
        final SecurityScheme rq = new SecurityScheme();
        rq.setDescription("Security schemes applicable in the application");
        final OAuthFlows flows = new OAuthFlows();
        final OAuthFlow flow = new OAuthFlow();
        flow.setTokenUrl(Runtime.get().getConfiguration("endpoint.security.tokenurl", "http://notfound404.domain"));
        flow.setScopes(scopes);
        flows.setClientCredentials(flow);
        rq.setFlows(flows);
        rq.setType(SecurityScheme.Type.OAUTH2);

        return rq;
    }

    private SecurityRequirement mgmRoleSecurityRequirements(String name) {
        SecurityRequirement requirement = null;
        final RoleMapping scopeMapping = RoleAuthorizer.get().getFunctionScopes("name");
        final RoleMapping.Definition mapping = scopeMapping.getMapping(name);
        if (mapping != null) {
            final Set<String> mgmRoles = mapping.getMgmRoles();
            if (!Utils.isEmpty(mgmRoles)) {
                requirement = new SecurityRequirement();
                requirement.addList(SCHEME_MGM_ROLE, new ArrayList<String>(mgmRoles));
            }
        }
        return requirement;
    }

    private SecurityRequirement serviceRoleSecurityRequirements(String name) {
        SecurityRequirement requirement = null;
        final RoleMapping scopeMapping = RoleAuthorizer.get().getFunctionScopes("name");
        final RoleMapping.Definition mapping = scopeMapping.getMapping(name);
        if (mapping != null) {
            final String serviceRole = mapping.getServiceRole();
            if (serviceRole != null) {
                requirement = new SecurityRequirement();
                requirement.addList(SCHEME_SERVICE_ROLE, Arrays.asList(serviceRole));
            }
        }
        return requirement;
    }

    private SecurityRequirement scopeSecurityRequirement(String name) {
        SecurityRequirement requirement = null;
        final RoleMapping scopeMapping = ScopeAuthorizer.get().getFunctionScopes("name");
        final RoleMapping.Definition mapping = scopeMapping.getMapping(name);
        if (mapping != null) {
            final Set<String> roles = mapping.getRoles();
            if (roles != null) {
                requirement = new SecurityRequirement();
                requirement.addList(SCHEME_SECURITY_NAME, new ArrayList<String>(roles));
            }
        }
        return requirement;
    }

}