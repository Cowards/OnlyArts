package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CategoryDAO;
import com.cowards.onlyarts.services.CommentDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("/v3/artworks")
public class Artwork {

    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();
    private static final CommentDAO commentDao = CommentDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final UserDAO userDao = UserDAO.getInstance();
    private static final CategoryDAO categoryDao = CategoryDAO.getInstance();

}
