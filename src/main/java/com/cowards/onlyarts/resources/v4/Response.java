package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.services.RequestDAO;
import com.cowards.onlyarts.services.ResponseDAO;
import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("v4/response")
public class Response {

    private final RequestDAO requestDAO = RequestDAO.getInstance();
    private final TokenDAO tokenDAO = TokenDAO.getInstance();
    private final ResponseDAO responseDAO = ResponseDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
}
