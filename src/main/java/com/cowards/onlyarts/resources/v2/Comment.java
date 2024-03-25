package com.cowards.onlyarts.resources.v2;

import com.cowards.onlyarts.core.CodeGenerator;
import com.cowards.onlyarts.repositories.comment.CommentDTO;
import com.cowards.onlyarts.repositories.token.TokenDTO;
import com.cowards.onlyarts.repositories.token.TokenERROR;
import com.cowards.onlyarts.services.CommentDAO;
import com.cowards.onlyarts.services.TokenDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * This class represents the endpoints for managing comments, including posting
 * comments to specific artworks and retrieving comments.
 */
@Path("v2/comments")
public class Comment {

    private static final CommentDAO commentDao = CommentDAO.getInstance();
    private static final TokenDAO tokenDao = TokenDAO.getInstance();

    /**
     * Endpoint for viewing comments on a specific artwork.
     *
     * @param artworkId The ID of the artwork to view comments for.
     * @return Response containing a list of comments for the specified artwork.
     */
    @GET
    @Path("/{artwork_id}")
    public Response viewComment(@PathParam("artwork_id") String artworkId) {
        List<CommentDTO> comment = commentDao.getArtworkComment(artworkId);
        if (!comment.isEmpty()) {
            return Response.ok(comment, MediaType.APPLICATION_JSON)
                    .build();
        } else {
            return Response.status(Response.Status.NO_CONTENT)
                    .build();
        }
    }

    /**
     * Endpoint for posting a comment to a specific artwork.
     *
     * @param tokenString The authentication token.
     * @param comment The comment data to post.
     * @return Response indicating success or failure of the comment posting.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response comment(@HeaderParam("authtoken") String tokenString,
            CommentDTO comment) {
        try {
            TokenDTO token = tokenDao.getToken(tokenString);
            String userId = token.getUserId();
            String commentId = CodeGenerator.generateUUID(20);
            comment.setCommentId(commentId);
            comment.setCommenterId(userId);
            boolean checkAddNewComment = commentDao.addComment(comment);
            if (checkAddNewComment) {
                comment = commentDao.getComment(commentId);
                return Response.ok(comment).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();

        } catch (TokenERROR ex) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ex).build();
        }
    }

}
