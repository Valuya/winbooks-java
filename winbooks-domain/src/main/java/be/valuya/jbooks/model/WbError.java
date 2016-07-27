/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public interface WbError {

    String getCode();

    String getDescription();

    String getTarget();

    void setCode(String code);

    void setDescription(String description);

    void setTarget(String target);
    
}
