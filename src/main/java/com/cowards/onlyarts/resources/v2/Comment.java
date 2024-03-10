package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CommentDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Path;

@Path("v2/comments")
public class Comment {

    private static final CommentDAO commentDao = CommentDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final ArtworkDAO artworkDao = ArtworkDAO.getInstance();

}
