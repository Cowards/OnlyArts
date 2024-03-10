package com.cowards.onlyarts.resources.v4;

import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("v4/user")
public class User {

    private final UserDAO userDao = UserDAO.getInstance();
    private final TokenDAO tokenDao = TokenDAO.getInstance();
}
