package com.cowards.onlyarts.resources.v3;

import com.cowards.onlyarts.services.TokenDAO;
import com.cowards.onlyarts.services.UserDAO;
import jakarta.ws.rs.Path;

@Path("/v3/users")
public class User {

    private static final UserDAO userDao = UserDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

}
