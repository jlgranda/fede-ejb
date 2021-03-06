/*
 * Copyright (C) 2016 jlgranda
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
package org.jlgranda.fede.model.sales;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.jpapi.model.BussinesEntity;

/**
 *
 * @author jlgranda
 */
@Entity
@Table(name = "EMISSION_POINT")
@DiscriminatorValue(value = "EMPT")
@PrimaryKeyJoinColumn(name = "emissionPointId")
public class EmissionPoint  extends BussinesEntity {

    private static final long serialVersionUID = 2921099283173813039L;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "establishment_id", insertable=false, updatable=false, nullable=false)
    private Establishment establishment;

    public Establishment getEstablisment() {
        return establishment;
    }

    public void setEstablisment(Establishment establishment) {
        this.establishment = establishment;
    }
    
}
