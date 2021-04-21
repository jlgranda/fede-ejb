/*
 * Copyright (C) 2021 kellypaulinc
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jlgranda.fede.model.accounting;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jpapi.model.PersistentObject;

/**
 *
 * @author kellypaulinc
 */
@Entity
@Table (name = "CashBox_Detail")
@NamedQueries({ @NamedQuery (name="CashBoxDetail.findByName", query = "SELECT s FROM CashBoxDetail s WHERE s.name = ?1 and s.owner is null ORDER BY 1"),
@NamedQuery (name="CashBoxDetail.findByNameAndOwner", query = "SELECT s FROM CashBoxDetail s WHERE s.name = ?1 and s.owner = ?2 ORDER BY 1"),
@NamedQuery (name="CashBoxDetail.findByCashBox", query = "SELECT s FROM CashBoxDetail s WHERE s.cashBox = ?1 ORDER BY 1"),
})
public class CashBoxDetail extends PersistentObject<CashBoxDetail> implements Comparable<CashBoxDetail>, Serializable {
    
    @ManyToOne (optional = false, cascade = {CascadeType.ALL})
    @JoinColumn (name = "cashBox_id", insertable = true, updatable = true, nullable = true)
    CashBox cashBox;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)    
    private CashBoxDetail.DenominationType denomination_type;
            
    String denomination;
    BigDecimal valuer;
    Long quantity;
    BigDecimal amount;
    
    public enum DenominationType {
        BILL,
        MONEY;
    }
    
    public CashBox getCashBox() {
        return cashBox;
    }

    public void setCashBox(CashBox cashBox) {
        this.cashBox = cashBox;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public DenominationType getDenomination_type() {
        return denomination_type;
    }

    public void setDenomination_type(DenominationType denomination_type) {
        this.denomination_type = denomination_type;
    }

    public BigDecimal getValuer() {
        return valuer;
    }

    public void setValuer(BigDecimal valuer) {
        this.valuer = valuer;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(17, 31); // two randomly chosen prime numbers
        // if deriving: appendSuper(super.hashCode()).
        hcb.append(getId());

        return hcb.toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CashBoxDetail other = (CashBoxDetail) obj;
        EqualsBuilder eb = new EqualsBuilder();
        
        eb.append(getId(), other.getId()).append(getDenomination(), other.getDenomination());
        return eb.isEquals();
    }
    
    @Override
    public int compareTo(CashBoxDetail other) {
        return this.createdOn.compareTo(other.getCreatedOn());
    }
}



