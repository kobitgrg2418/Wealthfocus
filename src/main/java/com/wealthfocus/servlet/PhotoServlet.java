package com.wealthfocus.servlet;

import com.wealthfocus.dao.UserDAO;
import com.wealthfocus.model.User;
import com.wealthfocus.util.SessionManager;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@WebServlet("/profile/photo")
public class PhotoServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Optional<String> userIdOpt = SessionManager.getUserId(session);
        if (userIdOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            User user = userDAO.findById(userIdOpt.get());
            if (user == null || user.getPhotoPath() == null || user.getPhotoPath().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Path photo = ProfileServlet.PHOTO_DIR.resolve(user.getPhotoPath());
            if (!Files.exists(photo)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String mime = Files.probeContentType(photo);
            if (mime == null) mime = "image/jpeg";
            resp.setContentType(mime);
            resp.setContentLengthLong(Files.size(photo));
            resp.setHeader("Cache-Control", "private, max-age=3600");

            try (OutputStream out = resp.getOutputStream()) {
                Files.copy(photo, out);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
