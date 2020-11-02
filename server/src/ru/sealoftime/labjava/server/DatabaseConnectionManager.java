package ru.sealoftime.labjava.server;

import lombok.SneakyThrows;
import org.apache.logging.log4j.core.jmx.Server;
import ru.sealoftime.labjava.core.model.data.concrete.*;
import ru.sealoftime.labjava.core.util.UnsafeFunction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Properties;

public class DatabaseConnectionManager {
    private String username;
    private String password;
    private String dbUrl;

    public Connection connect() throws SQLException{ return DriverManager.getConnection(dbUrl, username, password); }

    public DatabaseConnectionManager(){
        Properties config = new Properties();
        try {
            var fis = new FileInputStream("config.properties");
            config.load(fis);

            username = config.getProperty("postgresql.username");
            password = config.getProperty("postgresql.password");
            dbUrl    = config.getProperty("postgresql.db_url");

            var dbDriver = Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            ServerApplication.logger.error("Driver for the database has not been found. ABORT");
            System.exit(-1);
            //TODO: proper shutdown
        }
    }

    public int performQuery(UnsafeFunction<Connection, Integer, SQLException> query){
        try(var con = connect()){
            return query.apply(con);
        }catch(SQLException e){
            e.printStackTrace();
            ServerApplication.logger.error("Database error #{}. SQLState: {};  Message: {}", e.getErrorCode(), e.getSQLState(), e.getMessage());
        }
        return -1;
    }

    public int performQueryWithOneStatement(String sql, UnsafeFunction<PreparedStatement, Integer, SQLException> query){
        return this.performQuery((con)->{
            try(var stmt = con.prepareStatement(sql)){
                return query.apply(stmt);
            }
        });
    }

    public int checkCredentials(String login, String passwordHash) {
        try(var connection = ServerApplication.dcm.connect();
            var stmt = connection.prepareStatement("SELECT user_id FROM users WHERE login = ? AND password = ?")){
            stmt.setString(1, login);
            stmt.setString(2, get_SHA_512_SecurePassword(passwordHash, "whyDoIStillExist"));
            var rs = stmt.executeQuery();
            if(rs.next())
                return rs.getInt(1);
            return -1;//invalid password
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -2;
    }
    public String get_SHA_512_SecurePassword(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    public void createTables(){
        try(var con = connect()){
            con.setAutoCommit(false);
            createSpaceMarines(con);
            createsUsers(con);
            createOwnerships(con);
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException throwables) {
            ServerApplication.logger.error(
                    throwables.getErrorCode() + " " + throwables.getSQLState()
            );
            throwables.printStackTrace();
        }
    }
    private void createOwnerships(Connection con) throws SQLException{
        try(var createOwnerships = con.prepareStatement(
                "CREATE TABLE Ownerships(" +
                        "ownership_id SERIAL PRIMARY KEY, " +
                        "owner_id INT references Users (user_id)," +
                        "posession_id INT references Space_Marines (spacemarine_id) on delete cascade " +
                        ")"
        )){
            createOwnerships.executeUpdate();
        }
    }
    private void createsUsers(Connection con) throws SQLException{
        try(var createUsers = con.prepareStatement(
                "CREATE TABLE Users(" +
                        "user_id SERIAL PRIMARY KEY," +
                        "login VARCHAR(20) UNIQUE NOT NULL," +
                        "password TEXT NOT NULL" +
                        ")"
        )){
            createUsers.executeUpdate();
        }
    }
    private void createSpaceMarines(Connection con) throws SQLException{
        try(var createSpaceMarines = con.prepareStatement(
                "CREATE TABLE Space_Marines(" +
                        "spacemarine_id SERIAL PRIMARY KEY," +
                        "name TEXT NOT NULL," +
                        "coordinates_x FLOAT NOT NULL," +
                        "coordinates_y BIGINT NOT NULL," +
                        "creationDate TIMESTAMP NOT NULL," +
                        "health INT NOT NULL," +
                        "category TEXT," +
                        "weaponType TEXT NOT NULL," +
                        "meleeWeapon TEXT NOT NULL," +
                        "chapter_name TEXT," +
                        "marinesCount BIGINT" +
                        ")"
        )){
            createSpaceMarines.executeUpdate();
        }
    }
}
