package org.mattyo161.commons.util;

import java.sql.*;

interface IConnection2Db {
    Connection getConnection(String server,
            String port,
            String user,
            String password)
            throws Exception;
    Connection getConnection(String server,
            String port,
            String user,
            String password,
            String db)
            throws Exception;
    Connection getConnection(String server,
            String port,
            String user,
            String password,
            String db,
            String options)
            throws Exception;
}