package com.mgmresorts.loyalty.data.impl;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.telemetry.TelemetryRecorder;
import com.mgmresorts.loyalty.data.entity.PlayerComment;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.dto.customer.CustomerComment;

public class CommentAccessTest {
    @Mock
    private EntityManager workManager;

    @Mock
    private SqlSupport<List<CustomerComment>, String> sqlSupportMock;

    @Mock
    private TelemetryRecorder recorder;

    @InjectMocks
    private CommentAccess mockPatronCustomerCommentAccess = new CommentAccess() {
        @Override
        protected EntityManager entityManager(DB db) {
            return workManager;
        }
    };

    @BeforeEach
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    @BeforeAll
    public static void init() {
        System.setProperty("runtime.environment", "local");

    }

    @Test
    public void databaseCallNative() throws Exception {
        StoredProcedureQuery storedProcedureQueryMock = Mockito.mock(StoredProcedureQuery.class);
        Mockito.doAnswer(invocation -> {
            return storedProcedureQueryMock;
        }).when(workManager).createNamedStoredProcedureQuery(any(String.class));

        Mockito.doAnswer(invocation -> {
            List<PlayerComment> playerComments = new ArrayList<PlayerComment>();
            PlayerComment playerComment = new PlayerComment();
            String result = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + "  <Header>\r\n"
                    + "    <TimeStamp>2018-04-10T10:01:04.713</TimeStamp>\r\n" + "    <Operation Data=\"Comments\" Operand=\"Information\" />\r\n" + "  </Header>\r\n"
                    + "  <PlayerID>4387797</PlayerID>\r\n" + "  <Body>\r\n" + "    <Comments>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n"
                    + "        <Number>1</Number>\r\n" + "        <IsPrivate>false</IsPrivate>\r\n"
                    + "        <Text>This customer has enrolled on a Do Not Call list.  It is against federal law and MGM MIRAGE policy to contact this customer by phone if the customer does not have an EBR with MGM MIRAGE.  Please review the customer’s play and trip history to verify that the player has an EBR prior to contacting the customer.</Text>\r\n"
                    + "        <CreatedDate>2007-07-13T01:14:11.250</CreatedDate>\r\n" + "        <ExpirationDate>2012-07-13T01:14:11.250</ExpirationDate>\r\n"
                    + "        <EnteredBy>\r\n" + "          <User>\r\n" + "            <UserID>32963</UserID>\r\n" + "            <LoginName>SYSTEM</LoginName>\r\n"
                    + "            <FirstName>SYSTEM</FirstName>\r\n" + "            <LastName>SYSTEM</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n"
                    + "        <Site />\r\n" + "      </Comment>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n"
                    + "        <Number>2</Number>\r\n" + "        <IsPrivate>false</IsPrivate>\r\n"
                    + "        <Text>Mr. ONeil requested special consideration to be able to make improper bets. This was denied by Christina. Mr. ONeil left before he could be told. Refer to Shift if necessary. L Tang</Text>\r\n"
                    + "        <CreatedDate>2007-11-18T17:30:52</CreatedDate>\r\n" + "        <ExpirationDate>2007-12-19T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n"
                    + "          <User>\r\n" + "            <UserID>34476</UserID>\r\n" + "            <LoginName>160000600</LoginName>\r\n"
                    + "            <FirstName>Lynn</FirstName>\r\n" + "            <LastName>Tang</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n"
                    + "        <Site>\r\n" + "          <SiteID>5</SiteID>\r\n" + "          <SiteDescription>MGM Detroit</SiteDescription>\r\n" + "        </Site>\r\n"
                    + "      </Comment>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>4</Number>\r\n"
                    + "        <IsPrivate>false</IsPrivate>\r\n" + "        <Text>qtd:4nts@cr p/ emurnane( 4/10</Text>\r\n"
                    + "        <CreatedDate>2008-03-31T07:18:57</CreatedDate>\r\n" + "        <ExpirationDate>2008-04-10T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n"
                    + "          <User>\r\n" + "            <UserID>37911</UserID>\r\n" + "            <LoginName>67636</LoginName>\r\n"
                    + "            <FirstName>Adriana</FirstName>\r\n" + "            <LastName>Lomeli</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n"
                    + "        <Site>\r\n" + "          <SiteID>1</SiteID>\r\n" + "          <SiteDescription>Mirage</SiteDescription>\r\n" + "        </Site>\r\n"
                    + "      </Comment>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>5</Number>\r\n"
                    + "        <IsPrivate>false</IsPrivate>\r\n" + "        <Text>ssn validated</Text>\r\n" + "        <CreatedDate>2008-06-19T09:21:36</CreatedDate>\r\n"
                    + "        <ExpirationDate>2008-06-20T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n" + "          <User>\r\n"
                    + "            <UserID>38449</UserID>\r\n" + "            <LoginName>67815</LoginName>\r\n" + "            <FirstName>Donna</FirstName>\r\n"
                    + "            <LastName>Lindsey</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n" + "        <Site />\r\n" + "      </Comment>\r\n"
                    + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>6</Number>\r\n" + "        <IsPrivate>false</IsPrivate>\r\n"
                    + "        <Text>PLEASE VERIFY THE ADDRESS ! ! ! ! !!</Text>\r\n" + "        <CreatedDate>2009-01-06T13:58:38</CreatedDate>\r\n"
                    + "        <ExpirationDate>2009-04-01T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n" + "          <User>\r\n"
                    + "            <UserID>37596</UserID>\r\n" + "            <LoginName>71090I</LoginName>\r\n" + "            <FirstName>Emiliana</FirstName>\r\n"
                    + "            <LastName>Parker</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n" + "        <Site />\r\n" + "      </Comment>\r\n"
                    + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>7</Number>\r\n" + "        <IsPrivate>false</IsPrivate>\r\n"
                    + "        <Text>Gst. has been explained the criteria for High Limiti's and is aware that he currently does not meet the criteria. </Text>\r\n"
                    + "        <CreatedDate>2010-12-31T23:25:24</CreatedDate>\r\n" + "        <ExpirationDate>2011-07-01T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n"
                    + "          <User>\r\n" + "            <UserID>55902</UserID>\r\n" + "            <LoginName>160011740I</LoginName>\r\n"
                    + "            <FirstName>Evan</FirstName>\r\n" + "            <LastName>Perelekos</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n"
                    + "        <Site>\r\n" + "          <SiteID>5</SiteID>\r\n" + "          <SiteDescription>MGM Detroit</SiteDescription>\r\n" + "        </Site>\r\n"
                    + "      </Comment>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>10</Number>\r\n"
                    + "        <IsPrivate>false</IsPrivate>\r\n"
                    + "        <Text>On 11/01/2017 while on BC 2012 Patron tore up a  a Bac card after a losing hand. He left before we could speak to him about not tearing cards.\r\n"
                    + "</Text>\r\n" + "        <CreatedDate>2017-11-02T01:20:55</CreatedDate>\r\n" + "        <ExpirationDate>2018-02-01T00:00:00</ExpirationDate>\r\n"
                    + "        <EnteredBy>\r\n" + "          <User>\r\n" + "            <UserID>34173</UserID>\r\n" + "            <LoginName>160001221</LoginName>\r\n"
                    + "            <FirstName>Henry</FirstName>\r\n" + "            <LastName>Duenas</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n"
                    + "        <Site>\r\n" + "          <SiteID>5</SiteID>\r\n" + "          <SiteDescription>MGM Detroit</SiteDescription>\r\n" + "        </Site>\r\n"
                    + "      </Comment>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>11</Number>\r\n"
                    + "        <IsPrivate>false</IsPrivate>\r\n"
                    + "        <Text>Mr. Oneil again tore a card on Bacc and was informed that he coould not tear any cards. He claimed that he has been doing it and it has been allowed. He was informed otherwise.  Please monitor when in action. BRAVO</Text>\r\n"
                    + "        <CreatedDate>2017-11-06T01:31:20</CreatedDate>\r\n" + "        <ExpirationDate>2018-06-01T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n"
                    + "          <User>\r\n" + "            <UserID>34092</UserID>\r\n" + "            <LoginName>160001705</LoginName>\r\n"
                    + "            <FirstName>Jeff</FirstName>\r\n" + "            <LastName>Bravo</LastName>\r\n" + "          </User>\r\n" + "        </EnteredBy>\r\n"
                    + "        <Site>\r\n" + "          <SiteID>5</SiteID>\r\n" + "          <SiteDescription>MGM Detroit</SiteDescription>\r\n" + "        </Site>\r\n"
                    + "      </Comment>\r\n" + "      <Comment>\r\n" + "        <IsHighPriority>true</IsHighPriority>\r\n" + "        <Number>12</Number>\r\n"
                    + "        <IsPrivate>false</IsPrivate>\r\n" + "        <Text>Please allow Mr. O’Neil in the VIP Lounge please </Text>\r\n"
                    + "        <CreatedDate>2018-04-05T10:58:28</CreatedDate>\r\n" + "        <ExpirationDate>2019-01-01T00:00:00</ExpirationDate>\r\n" + "        <EnteredBy>\r\n"
                    + "          <User>\r\n" + "            <UserID>50089</UserID>\r\n" + "            <LoginName>160002557</LoginName>\r\n"
                    + "            <FirstName>Kostantinos</FirstName>\r\n" + "            <LastName>Karagatsoulis</LastName>\r\n" + "          </User>\r\n"
                    + "        </EnteredBy>\r\n" + "        <Site>\r\n" + "          <SiteID>5</SiteID>\r\n" + "          <SiteDescription>MGM Detroit</SiteDescription>\r\n"
                    + "        </Site>\r\n" + "      </Comment>\r\n" + "    </Comments>\r\n" + "  </Body>\r\n" + "</CRMAcresMessage>\r\n" + "";
            playerComment.setResult(result);
            playerComments.add(playerComment);
            return playerComments;
        }).when(storedProcedureQueryMock).getResultList();
        List<PlayerComment> playerComments = mockPatronCustomerCommentAccess.databaseCallNative("4387797", true);
        Assertions.assertNotNull(playerComments);
        playerComments.forEach(comment -> {
            Assertions.assertNotNull(comment);
        });
    }

    @Test
    public void testGetEntityType() {
        Assertions.assertNotNull(mockPatronCustomerCommentAccess.getEntityType());
    }

}
