/*
 * Copyright (C) 2016 Jorge
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
package net.tecnopro.document.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.jlgranda.fede.model.document.DocumentType;
import org.jpapi.model.BussinesEntity;

/**
 *
 * @author Jorge
 */
@Entity
@Table(name = "documento")
@NamedQueries({
    @NamedQuery(name = "Documento.findLast", query = "select i FROM Documento i where i.owner=?1 ORDER BY i.id DESC"),
    @NamedQuery(name = "Documento.findLasts", query = "select i FROM Documento i where i.owner=?1 ORDER BY i.id DESC"),
    @NamedQuery(name = "Documento.findLastsByAuthor", query = "select i FROM Documento i where i.author=?1 ORDER BY i.id DESC"),
    @NamedQuery(name = "Documento.findLastsByOwner", query = "select i FROM Documento i where i.owner=?1 ORDER BY i.id DESC"),
    @NamedQuery(name = "Documento.countByOwner", query = "select count(i) FROM Documento i WHERE i.owner = ?1"),
    @NamedQuery(name = "Documento.findByTarea", query = "select i FROM Documento i WHERE i.tarea = ?1"),
    @NamedQuery(name = "Documento.countBussinesEntityByTagAndOwner", query = "select count(m.bussinesEntity) FROM Group g JOIN g.memberships m WHERE g.code=?1 and m.bussinesEntity.owner = ?2"),
    @NamedQuery(name = "Documento.countBussinesEntityByOwner", query = "select count(t) FROM Documento t WHERE t.owner = ?1")})

@XmlRootElement
public class Documento extends BussinesEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    private String numeroRegistro;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = true)
    private DocumentType documentType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    private String ruta;
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tarea;
@Transient
    private byte[] contents;
    public Documento() {
    }

    public Documento(String numeroRegistro, DocumentType documentType, String ruta, Tarea task) {
        this.numeroRegistro = numeroRegistro;
        this.documentType = documentType;
        this.ruta = ruta;
        this.tarea = task;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

}