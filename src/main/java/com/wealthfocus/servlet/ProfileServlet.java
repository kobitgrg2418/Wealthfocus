package com.wealthfocus.servlet;

import com.wealthfocus.dao.UserDAO;
import com.wealthfocus.model.User;
import com.wealthfocus.util.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/profile")
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 6 * 1024 * 1024
)
public class ProfileServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ProfileServlet.class.getName());
    static final Path PHOTO_DIR = Paths.get(System.getProperty("user.home"), ".wealthfocus", "photos");
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void init() throws ServletException {
        try {
            Files.createDirectories(PHOTO_DIR);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not create photo directory", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Optional<String> userIdOpt = SessionManager.getUserId(req.getSession());
        if (userIdOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            User user = userDAO.findById(userIdOpt.get());
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            req.setAttribute("user", user);
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Profile load error", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Optional<String> userIdOpt = SessionManager.getUserId(req.getSession());
        if (userIdOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String userId = userIdOpt.get();

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            user.setName(req.getParameter("name"));
            user.setEmail(req.getParameter("email"));
            user.setPhone(req.getParameter("phone"));
            user.setAddress(req.getParameter("address"));

            Part photoPart = req.getPart("photo");
            if (photoPart != null && photoPart.getSize() > 0) {
                String contentType = photoPart.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    String ext = extensionFor(contentType);
                    String filename = userId + ext;

                    deleteOldPhoto(userId);

                    Path dest = PHOTO_DIR.resolve(filename);
                    try (InputStream in = photoPart.getInputStream()) {
                        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                    user.setPhotoPath(filename);
                }
            }

            userDAO.update(user);

            req.setAttribute("user", user);
            req.setAttribute("success", "Profile updated successfully");
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Profile update error", e);
            req.setAttribute("error", "Failed to update profile. Please try again.");
            doGet(req, resp);
        }
    }

    private String extensionFor(String contentType) {
        switch (contentType) {
            case "image/png": return ".png";
            case "image/gif": return ".gif";
            case "image/webp": return ".webp";
            default: return ".jpg";
        }
    }

    private void deleteOldPhoto(String userId) {
        try {
            Files.list(PHOTO_DIR)
                .filter(p -> p.getFileName().toString().startsWith(userId + "."))
                .forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                });
        } catch (IOException ignored) {}
    }
}
