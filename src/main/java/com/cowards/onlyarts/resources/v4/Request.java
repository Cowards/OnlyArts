package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.services.RequestDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("v4/requests")
public class Request {

    private final RequestDAO requestDAO = RequestDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
}
