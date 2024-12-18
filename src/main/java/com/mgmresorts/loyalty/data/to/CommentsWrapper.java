
package com.mgmresorts.loyalty.data.to;

import java.util.List;

import com.mgmresorts.loyalty.dto.customer.CustomerComment;

public class CommentsWrapper {

    private List<CustomerComment> comments;

    public List<CustomerComment> getComments() {
        return comments;
    }

    public void setComments(List<CustomerComment> comments) {
        this.comments = comments;
    }

   
}
