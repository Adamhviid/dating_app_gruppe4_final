package com.example.demo.Repositories;

import com.example.demo.Models.Profile;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;

import java.io.IOException;

@Repository
public class ProfileRepository {

    //liste med alle liste af profiler
    List<Profile> allProfiles = new ArrayList<>();
    List<Profile> allCandidates = new ArrayList<>();


    //Denne metode laver forbindelsen til mysql databasen
    public Connection establishConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/dating_app_schema?serverTimezone=UTC", "root", "1");
        //standard: user=root, password=1
    }

    //Metode i stedet for dupliceret kode
    //Foretager en ps.execute og læser resulSet ind i allProfiles array
    public List<Profile> returnProfile(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        //lave resultattet om til objekter, og derefter ind i en arrayliste
        while (rs.next()) {
            Profile temp = new Profile(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getInt(7),
                    rs.getString(8),
                    rs.getBytes(9),
                    rs.getString(2));
            allProfiles.add(temp);
        }
        return allProfiles;
    }

    //display alle profiler i databasen
    public List<Profile> listAllProfiles() throws SQLException{
        allProfiles.clear();
        allCandidates.clear();
        PreparedStatement ps = establishConnection().prepareStatement(" SELECT * FROM profiles;");

        return returnProfile(ps);
    }

    //lav profil og indsæt data til databasen
    public void createProfile(String pName, String pKodeord, String pGender, String pEmail, String pDescription, int pAdmin, MultipartFile file) throws SQLException, IOException {
        allProfiles.clear();
        allCandidates.clear();
        byte[] fileAsBytes = file.getBytes();
        Blob fileAsBlob = new SerialBlob(fileAsBytes);
        //lavet et statement og eksekvere en query
        PreparedStatement ps = establishConnection().prepareStatement("INSERT INTO profiles (name, kodeord, gender,email,description, candidatelist, image) VALUES (?,?,?,?,?,?,?);");
        ps.setString(1,pName);
        ps.setString(2,pKodeord);
        ps.setString(3,pGender);
        ps.setString(4,pEmail);
        ps.setString(5,pDescription);
        ps.setString(6,",");
        ps.setBlob(7, fileAsBlob);

        ps.executeUpdate();
    }

    //checke om login-et
    public List<Profile> searchLogin(String email, String kodeord) throws SQLException {
        allProfiles.clear();
        allCandidates.clear();
        PreparedStatement ps = establishConnection().prepareStatement("SELECT * FROM profiles where email = ? AND kodeord = ?");
        ps.setString(1, email);
        ps.setString(2, kodeord);

        return returnProfile(ps);
    }

    public List<Profile> searchProfile(String gender) throws SQLException {
        allProfiles.clear();
        allCandidates.clear();
        PreparedStatement ps = establishConnection().prepareStatement("SELECT * FROM profiles where gender like ?");
        ps.setString(1,gender);

        return returnProfile(ps);
    }

    // Finder bruger med x id
    public List<Profile> profile(int id) throws SQLException {
        allProfiles.clear();
        allCandidates.clear();
        PreparedStatement ps = establishConnection().prepareStatement("SELECT * FROM profiles where id = ?");
        ps.setInt(1, id);

        return returnProfile(ps);
    }

    //tilføj profilen til currentlogin favoritliste
    public void addCandidate(String candidateId, int currentId) throws SQLException {
        PreparedStatement ps = establishConnection().prepareStatement("UPDATE profiles SET candidatelist=concat(candidatelist, ?) where id = ?");
        ps.setString(1,candidateId + ",");
        ps.setInt(2,currentId);
        ps.executeUpdate();
    }

    //display currentlogin favoritliste
    public List<Profile> candidateList(int currentId) throws SQLException {
        allProfiles.clear();
        allCandidates.clear();
        String candidateList = "";
        PreparedStatement ps = establishConnection().prepareStatement("SELECT candidatelist FROM profiles WHERE id = ?");
        ps.setInt(1,currentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            candidateList = rs.getString(1);
        }
        String[] candidateAraray = candidateList.split(",");

        for ( int i = 0; i <= candidateAraray.length-1; i++) {
            PreparedStatement pss = establishConnection().prepareStatement("SELECT * FROM profiles WHERE id = ?");
            pss.setString(1, candidateAraray[i]);
            allCandidates = returnProfile(pss);
        }
        System.out.println(allCandidates.toString());
        return allCandidates;
    }

    //inde på profilen som er loggede ind, kan ændre sine oplysninger
    public List<Profile> editProfile(int id, String name, String gender, String email, String kodeord, String description) throws SQLException {
        PreparedStatement ps = establishConnection().prepareStatement("UPDATE profiles SET name = ?, gender = ?, email = ?, description = ?, kodeord = ? where id= ?");
        ps.setString(1,name);
        ps.setString(2,gender);
        ps.setString(3,email);
        ps.setString(4,description);
        ps.setString(5,kodeord);
        ps.setInt(6,id);
        ps.executeUpdate();

        PreparedStatement pss = establishConnection().prepareStatement("SELECT * FROM profiles where id = ?");
        pss.setInt(1,id);
        return returnProfile(pss);
    }

    //for admins, så de kan slette profiler
    public void deleteProfile(int id) throws SQLException {
        PreparedStatement ps = establishConnection().prepareStatement("DELETE FROM profiles WHERE id=?");
        ps.setInt(1,id);
        ps.executeUpdate();
    }












}
