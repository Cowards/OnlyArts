package com.cowards.onlyarts.resources.v1;

import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("v1/authentication")
public class Authentication {

    private static final UserDAO userDao = UserDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

}
