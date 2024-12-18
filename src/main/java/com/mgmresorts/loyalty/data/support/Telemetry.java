package com.mgmresorts.loyalty.data.support;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.telemetry.DependencyData;
import com.mgmresorts.loyalty.data.support.SqlSupport.DB;

public class Telemetry {
    public static class MssqlDependency extends DependencyData {

        private MssqlDependency(String uri) {
            super(uri);
        }

        public static DependencyData init(DB db) {
            return new MssqlDependency(Runtime.get().getConfiguration("database." + db.name().toLowerCase() + ".connection.url"));
        }

        public String getType() {
            return "MSSql.Native";
        }

    }
}
