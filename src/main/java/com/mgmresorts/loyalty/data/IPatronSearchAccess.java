package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;

public interface IPatronSearchAccess<T> {
    interface Query {

        String GET_PATRON_CUSTOMER_BY_FIRSTNAME_LASTNAME = "SELECT DISTINCT p.PlayerID, pt.title, pn.firstname, pn.lastname, p.birthday"
                + " FROM PlayerManagement.dbo.Player AS p WITH (NOLOCK)" + " INNER JOIN PlayerManagement.dbo.PlayerName as pn with (NOLOCK) ON p.PlayerID = pn.PlayerID"
                + " LEFT JOIN PlayerManagement.dbo.PlayerTitle as pt with (nolock) on pn.Titleid = pt.titleid"
                + " WHERE 1=1 AND p.Status = 'A' AND p.Status = 'A'  AND pn.firstName like ? and pn.lastName = ?";

        String GET_PATRON_CUSTOMER_BY_FIRSTNAME_LASTNAME_DOB = "SELECT DISTINCT p.PlayerID, pt.title, pn.firstname, pn.lastname, p.birthday  "
                + "FROM PlayerManagement.dbo.Player AS p WITH (NOLOCK) INNER JOIN PlayerManagement.dbo.PlayerName as pn with (NOLOCK) ON p.PlayerID = pn.PlayerID  "
                + "LEFT JOIN PlayerManagement.dbo.PlayerTitle as pt with (nolock) on pn.Titleid = pt.titleid  "
                + "WHERE 1=1 AND p.Status = 'A'  AND pn.firstName like ? and pn.lastName = ? and p.birthday= ?";

        String GET_PATRON_CUSTOMER_BY_FIRSTNAME_LASTNAME_EMAIL_POSTALCODE = "SELECT DISTINCT p.PlayerID, pt.title, pn.firstname, pn.lastname, p.birthday, pe.email  "
                + "FROM PlayerManagement.dbo.Player AS p WITH (NOLOCK)  INNER JOIN PlayerManagement.dbo.PlayerName as pn "
                + "with (NOLOCK) ON p.PlayerID = pn.PlayerID  LEFT JOIN PlayerManagement.dbo.PlayerTitle as pt with (nolock) on pn.Titleid = pt.titleid  "
                + "INNER JOIN PlayerManagement.dbo.PlayerEmail AS pe WITH (NOLOCK) ON  p.PlayerID = pe.PLayerID   "
                + "INNER JOIN PlayerManagement.dbo.PlayerAddress AS pa WITH (NOLOCK) ON p.PlayerID = pa.PLayerID  "
                + "WHERE 1=1 AND p.Status = 'A'  AND pn.firstName like ? and pn.lastName = ? and pa.zipcode = ? and rtrim(pe.email) = ?";

        String GET_PATRON_CUSTOMER_BY_EMAIL = "SELECT DISTINCT p.PlayerID, pt.title, pn.firstname, pn.lastname, p.birthday, pe.email  "
                + "FROM PlayerManagement.dbo.Player AS p WITH (NOLOCK)  INNER JOIN PlayerManagement.dbo.PlayerName as pn with (NOLOCK) ON p.PlayerID = pn.PlayerID  "
                + "LEFT JOIN PlayerManagement.dbo.PlayerTitle as pt with (nolock) on pn.Titleid = pt.titleid  "
                + "INNER JOIN PlayerManagement.dbo.PlayerEmail AS pe WITH (NOLOCK) ON  p.PlayerID = pe.PLayerID  "
                + "WHERE 1=1 AND p.Status = 'A'  AND rtrim(pe.email) = ?";

        String GET_PATRON_CUSTOMER_BY_PHONE = "SELECT DISTINCT p.PlayerID, pt.title, pn.firstname, pn.lastname, p.birthday  FROM PlayerManagement.dbo.Player AS p WITH (NOLOCK)  "
                + "INNER JOIN PlayerManagement.dbo.PlayerName as pn with (NOLOCK) ON p.PlayerID = pn.PlayerID  "
                + "LEFT JOIN PlayerManagement.dbo.PlayerTitle as pt with (nolock) on pn.Titleid = pt.titleid  "
                + "INNER JOIN PlayerManagement.dbo.PlayerPhone AS pp WITH (NOLOCK)  ON p.PlayerID = pp.PLayerID  " + "WHERE 1=1 AND p.Status = 'A'  AND pp.phone = ?";
    }

    List<T> getByFirstAndLastName(String firstName, String lastName) throws AppException;

    List<T> getByFirstAndLastNameAndDob(String firstName, String lastName, String dob) throws AppException;

    List<T> getByFirstAndLastNameAndEmailAndPostalCode(String firstName, String lastName, String email, String postalCode) throws AppException;

    List<T> getByEmail(String email) throws AppException;

    List<T> getByPhone(String phone) throws AppException;
}
