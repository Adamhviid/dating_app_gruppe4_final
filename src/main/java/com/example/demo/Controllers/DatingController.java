package com.example.demo.Controllers;

import com.example.demo.Models.Message;
import com.example.demo.Models.Profile;
import com.example.demo.Repositories.MessageRepository;
import com.example.demo.Repositories.ProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DatingController {

    ProfileRepository rp = new ProfileRepository();
    MessageRepository mp = new MessageRepository();
    List<Profile> allProfiles = new ArrayList<>();
    List<Profile> allCandidates = new ArrayList<>();
    List<Message> allMessages = new ArrayList<>();
    Profile currentLogin = new Profile(0,null,null,null,null,null,0,null,null,null);

    // Root
    @GetMapping("/")
    public String index(Model profileModel) throws SQLException{
        profileModel.addAttribute("profile", rp.listAllProfiles());
        return "index";
    }

    // Create profile
    @PostMapping("/createprofile")
    public String createProfile(WebRequest createProfileData, @RequestParam("file") MultipartFile file) throws SQLException, IOException {
        int admin = 0;
        String name = createProfileData.getParameter("pName");
        String gender = createProfileData.getParameter("pGender");
        String email = createProfileData.getParameter("pEmail");
        String description = createProfileData.getParameter("pDescription");
        String kodeord = createProfileData.getParameter("pKodeord");
        rp.createProfile(name, kodeord, gender, email, description, admin,file);

        return "login";
    }

    //Login side
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //checking if login is correct
    @PostMapping("/correctlogin")
    public String login(WebRequest loginData)  throws SQLException{
        String email = loginData.getParameter("pEmail");
        String kodeord = loginData.getParameter("pKodeord");
        allProfiles = rp.searchLogin(email,kodeord);

        try {
            currentLogin = allProfiles.get(0);
            if(currentLogin.getAdmin()==1){
                System.out.println("hej");
                return "redirect:/adminPage";
            }
            System.out.println("logged in as " + allProfiles.get(0).toString());
        } catch (IndexOutOfBoundsException e) {
            return "errorlogin";
        }
        return "main";
    }

    //admin page
    @GetMapping("/adminPage")
    public String admin(Model m) throws SQLException{
        allProfiles = rp.listAllProfiles();
        m.addAttribute("allProfiles", allProfiles);
        return "adminPage";
    }

    //delete profil ny metode
    @PostMapping("/deleteprofile")
    public String deleteProfile(WebRequest deleteProfile) {
        try {
            String id = deleteProfile.getParameter("delete-admin");
            rp.deleteProfile(Integer.parseInt(id));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "redirect:/adminPage";
    }

    // Search Profiles
    @GetMapping("/main")
    public String searchProfiles(Model searchModel, WebRequest searchProfile){
        String gender = searchProfile.getParameter("pGender");
        try {
            allProfiles = rp.searchProfile(gender);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchModel.addAttribute("profileList",allProfiles);
        return "main";
    }

    //myprofile
    @GetMapping("/myprofile")
    public String myprofile(Model myprofileModel) throws SQLException {
        allProfiles.clear();
        allProfiles.add(currentLogin);
        myprofileModel.addAttribute("profileID",allProfiles);
        return "myprofile";
    }

    //til main side fra profil
    @PostMapping("/tomain")
    public String toMain() {
        return "main";
    }

    // Edit Profile
    @PostMapping("/editprofile")
    public String editProfile(WebRequest editProfile) throws IOException {
        allProfiles.clear();
        allCandidates.clear();
        try {
            int id = currentLogin.getId();
            //int id = Integer.parseInt(editProfile.getParameter("eId"));
            String name = editProfile.getParameter("eName");
            String gender = editProfile.getParameter("eGender");
            String email = editProfile.getParameter("eEmail");
            String description = editProfile.getParameter("eDescription");
            String kodeord = editProfile.getParameter("eKodeord");
            allProfiles = rp.editProfile(id,name,gender,email,description,kodeord);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "redirect:/myprofile";
    }

    //see messages
    @PostMapping("/myprofile/messages")
    public String seeMessages(Model m, Model myprofileModel) throws SQLException {
        allMessages = mp.seeMessage(currentLogin.getId());
        m.addAttribute("messages",allMessages);
        myprofileModel.addAttribute("profileID",allProfiles);
        return "myprofile";
    }

    //display liste af profiles favoritter
    @PostMapping("/maincandidate")
    public String searchCandidates(Model candidateModel) throws SQLException {
        allCandidates = rp.candidateList(currentLogin.getId());
        candidateModel.addAttribute("candidateList",allCandidates);
        return "main";
    }

    //dynamisk profilside til hver profil
    @GetMapping("/profile")
    public String profile(Model profileModel) {
        profileModel.addAttribute("profileList",allProfiles);
        return "profile";
    }

    //finde profilens ID og derefter display deres side
    @PostMapping("/profileId")
    public String getProfile(WebRequest profileClick){
        String id = profileClick.getParameter("profileId");
        System.out.println(id);

        try {
            allProfiles = rp.profile(Integer.parseInt(id));
            allCandidates = rp.profile(Integer.parseInt(id));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("hov");
        }
        return "redirect:/profile";
    }

    //Send message til profil fra currentlogin
    @PostMapping("/sendmessage")
    public String sendMessage(WebRequest receiverBtn, WebRequest messageInput) throws SQLException{
        int receiverId = Integer.parseInt(receiverBtn.getParameter("getReceiverId"));
        String msg = messageInput.getParameter("getMessage");
        mp.sendMessage(currentLogin.getId(),receiverId,msg);
        return "redirect:/profile";
    }

    //tilføj favort til current login
    @PostMapping("/addtokandidat")
    public String kandidatList(WebRequest kandidatButton) throws SQLException {
        String candidateId = kandidatButton.getParameter("addToKandidat");
        int currentId = currentLogin.getId();
        rp.addCandidate(candidateId,currentId);
        System.out.println("Hej");
        return "redirect:/profile";
    }

    //Om os side
    @GetMapping("/omos")
    public String omos() {
        return "omos";
    }

    //Sugar Mommy side
    @GetMapping("/sugarmommy")
    public String sugarmommy() {
        return "sugarmommy";
    }

    //Sugar Daddy side
    @GetMapping("/sugardaddy")
    public String sugardaddy() {
        return "sugardaddy";
    }
}




