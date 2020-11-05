package com.example.demo.repositories;

import com.example.demo.Models.Profile;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProfileRepository {

    //liste med alle profiler
    List<Profile> allProfiles = new ArrayList<Profile>();

    //Denne metode laver forbindelsen til mysql databasen
    private Connection establishConnection() throws SQLException {
        Connection connectionToDB = DriverManager.getConnection("jdbc:mysql://localhost:3306/dating_app", "root", "Viggo213");
        return connectionToDB;
    }

    public List<Profile> listAllProfiles() {
        allProfiles.clear();
        try {
            //lavet et statement og eksekvere en query
            PreparedStatement ps = establishConnection().prepareStatement("SELECT * FROM profiles;");
            ResultSet rs = ps.executeQuery();

            //lave resultattet om til objekter, og derefter ind i en arrayliste
            while (rs.next()) {
                Profile tmp = new Profile(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5));
                allProfiles.add(tmp);
            }

        } catch (SQLException e) {
            return null;
        }
        return allProfiles;
    }


    public void createProfile(String pName, String pGender, String pEmail, String pDescription) throws SQLException {
        allProfiles.clear();
        //lavet et statement og eksekvere en query
        PreparedStatement ps = establishConnection().prepareStatement("INSERT INTO profiles (name,gender,email,description) VALUES (?,?,?,?);");

        ps.setString(1,pName);
        ps.setString(2,pGender);
        ps.setString(3,pEmail);
        ps.setString(4,pDescription);

        int rs = ps.executeUpdate();

        PreparedStatement pss = establishConnection().prepareStatement("SELECT * FROM profiles LIMIT ?,1");
        pss.setInt(1,rs);
        ResultSet rss = pss.executeQuery();

        //lave resultattet om til objekter, og derefter ind i en arrayliste
        while (rss.next()) {
            Profile temp = new Profile(
                    rss.getInt(1),
                    rss.getString(2),
                    rss.getString(3),
                    rss.getString(4),
                    rss.getString(5));
            allProfiles.add(temp);

        }
    }

    public void deleteProfile(int id) throws SQLException {
        PreparedStatement ps = establishConnection().prepareStatement("DELETE FROM profiles WHERE id=?");
        ps.setInt(1,id);
        ps.executeUpdate();
    }

    public void editProfile(int id, String name, String gender, String email, String description) throws SQLException {
        PreparedStatement ps = establishConnection().prepareStatement("UPDATE profiles SET name = ?, gender = ?, email = ?, description = ? where id= ?");
        ps.setString(1,name);
        ps.setString(2,gender);
        ps.setString(3,email);
        ps.setString(4,description);
        ps.setInt(5,id);
        ps.executeUpdate();
    }

    public List<Profile> searchProfile(String gender) throws SQLException {
        allProfiles.clear();
        PreparedStatement ps = establishConnection().prepareStatement("SELECT * FROM profiles where gender like ?");
        ps.setString(1,"%" + gender + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Profile temp = new Profile(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5));
            allProfiles.add(temp);
        }
        return allProfiles;
    }
}
