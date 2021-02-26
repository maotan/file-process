package com.example.file.model;

public class PatentSecond {
    private Long id;
    private String title;

    private String applyNum;
    private String applyTime;
    /** 公告号 */
    private String publicNum;
    /** 公告日 */
    private String publicTime;
    /** 主法律状态 */
    private String mainLawState;
    /** 次法律状态 */
    private String secondLawState;
    /** 分类号 */
    private String classNum;
    /** 申请人 */
    private String applicant;
    /** 地址 */
    private String address;
    /** 当前申请人 */
    private String presentApplicant;
    /** 当前地址 */
    private String presentAddress;
    /** 发明人 */
    private String inventor;
    /** 优先权 */
    private String priority;
    /** 专利代理机构*/
    private String agency;
    /** 代理人*/
    private String agentPerson;
    /** 摘要 */
    private String summary;
    /** 图片url*/
    private String picUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getPublicNum() {
        return publicNum;
    }

    public void setPublicNum(String publicNum) {
        this.publicNum = publicNum;
    }

    public String getPublicTime() {
        return publicTime;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime = publicTime;
    }

    public String getMainLawState() {
        return mainLawState;
    }

    public void setMainLawState(String mainLawState) {
        this.mainLawState = mainLawState;
    }

    public String getSecondLawState() {
        return secondLawState;
    }

    public void setSecondLawState(String secondLawState) {
        this.secondLawState = secondLawState;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPresentApplicant() {
        return presentApplicant;
    }

    public void setPresentApplicant(String presentApplicant) {
        this.presentApplicant = presentApplicant;
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    public String getInventor() {
        return inventor;
    }

    public void setInventor(String inventor) {
        this.inventor = inventor;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgentPerson() {
        return agentPerson;
    }

    public void setAgentPerson(String agentPerson) {
        this.agentPerson = agentPerson;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
