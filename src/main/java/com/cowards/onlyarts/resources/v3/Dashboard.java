package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.repositories.artwork.ArtworkDTO;
import com.cowards.onlyarts.repositories.comment.CommentDTO;
import com.cowards.onlyarts.repositories.order.OrderDTO;
import com.cowards.onlyarts.repositories.order.OrderERROR;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.repositories.user.UserDTO;
import com.cowards.onlyarts.repositories.user.UserERROR;
import com.cowards.onlyarts.services.ArtworkDAO;
import com.cowards.onlyarts.services.CommentDAO;
import com.cowards.onlyarts.services.OrderDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Path("/v3/dashboard")
public class Dashboard {

    private static final ArtworkDAO artworkDAO = ArtworkDAO.getInstance();
    private static final UserDAO userDAO = UserDAO.getInstance();
    private static final OrderDAO orderDAO = OrderDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final CommentDAO commentDao = CommentDAO.getInstance();

}
