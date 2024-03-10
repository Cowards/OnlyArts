package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.services.ReactionDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Path;

@Path("v2/reaction")
public class Reaction {

    private static final TokenDAO tokenDao = TokenDAO.getInstance();
    private static final ReactionDAO reactionDao = ReactionDAO.getInstance();

}
