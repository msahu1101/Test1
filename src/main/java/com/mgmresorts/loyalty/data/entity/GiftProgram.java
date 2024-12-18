package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "GiftPoints.dbo.GiftProgram")
@Entity
public class GiftProgram {
    @Id
    @Column(name = "GiftProgramID")
    private int giftProgramId;

    @Column(name = "GiftProgramTitle")
    private String giftProgramTitle;

    public int getGiftProgramId() {
        return giftProgramId;
    }

    public String getGiftProgramTitle() {
        return giftProgramTitle;
    }

    public void setGiftProgramId(int giftProgramId) {
        this.giftProgramId = giftProgramId;
    }

    public void setGiftProgramTitle(String giftProgramTitle) {
        this.giftProgramTitle = giftProgramTitle;
    }

    @Override
    public String toString() {
        return "[ProgramId = " + getGiftProgramId() + " , ProgramTitle = " + getGiftProgramTitle() + "]";
    }
}