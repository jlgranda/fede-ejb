/*
 * Copyright (C) 2015 jlgranda
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
package org.jlgranda.fede.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.jpapi.model.Setting;
import org.jpapi.controller.BussinesEntityHome;
import org.jpapi.model.CodeType;
import org.jpapi.model.Group;
import org.jpapi.model.StatusType;
import org.jpapi.model.profile.Subject;
import org.jpapi.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jlgranda
 */
@Stateless
public class SetupService implements Serializable {

    private static Logger log = LoggerFactory.getLogger(SetupService.class);
    private static final long serialVersionUID = 235471466504044866L;

    @PersistenceContext
    EntityManager em;

    protected BussinesEntityHome bussinesEntityHome = null;

    private static final String[][] DATA = {
        {"0", "PLAN DE CUENTAS SEGUN NIIF COMPLETAS Y NIIF PARA PYMES", "SCHEMA", "Plan de cuentas según NIIF Completas y NIIF para PYMES"},
        {"1", "ACTIVO", "GENDER", "Conforman el estado de situación financiera, de flujo de efectivo y de evolución patrimonio"},
        {"10101", "EFECTIVO Y EQUIVALENTES AL EFECTIVO", "ACCOUNT", "Efectivo, comprende la caja y los depósitos bancarios a la vista.<br/>Equivalente al efectivo, son inversiones a corto plazo de gran liquidez (hasta 90 días), que son fácilmente convertibles en valores en efectivo, con riesgo insignificante de cambios en su valor. <br/>NIC 7p6, 7, 8, 9, 48, 49 NIIF para PYMES p7.2"},
        {"10102", "ACTIVOS FINANCIEROS", "ACCOUNT", "NIC 32 p11 - NIC 39 - NIIF 7 - NIIF 9 <br/>Secciones 11 y 12 NIIF para las PYMES"},
        {"1010201", "ACTIVOS FINANCIEROS A VALOR RAZONABLE CON CAMBIOS EN RESULTADOS", "ACCOUNT", "Son los activos financieros adquiridos para negociar activamente, con el objetivo de generar ganancia. <br/>Medición Inicial y Posterior:<br/>A valor razonable<br/>La variación se reconoce resultado del ejercicio"},
        {"2", "PASIVO", "GENDER", "Conforman el estado de situación financiera, de flujo de efectivo y de evolución patrimonio"},
        {"3", "PATRIMONIO", "GENDER", "Conforman el estado de situación financiera, de flujo de efectivo y de evolución patrimonio"},
        {"4", "CUENTAS DE RESULTADO ACREEDORAS", "ASSET", "Cuentas de gestión de partidas de resultados acreedoras y deudoras, indispensable para la elaboración del balance de perdidas y ganancias"},
        {"5", "CUENTAS DE RESULTADO DEUDORAS", "LIABILITY", "Cuentas de gestión de partidas de resultados acreedoras y deudoras, indispensable para la elaboración del balance de perdidas y ganancias"},
        {"6", "CUENTAS CONTINGENTES", "INCOME", "Agrupan las obligaciones eventuales"},
        {"7", "CUENTAS DE ORDEN", "EXPENSE", "Cuentas de orden y de control, indispensables para la buena administración"}
    };

    public void validate() {
        //bussinesEntityHome.setEntityManager(bussinesEntityRepository);
        validateDB();
        //validateIdentityObjectTypes();
        validateSecurity();
        validateDefaultCustomer();
        validateDefaultSupplier();
        validateSetting();

    }

    private void validateDB() {
        Setting singleResult = null;
        try {
            TypedQuery<Setting> query = getEntityManager().createQuery("from Setting s where s.name='schemaVersion'",
                    Setting.class);
            singleResult = query.getSingleResult();
        } catch (NoResultException e) {
            java.util.Date now = Dates.now();
            singleResult = new Setting("schemaVersion", "1");
            singleResult.setCreatedOn(now);
            singleResult.setLastUpdate(now);
            singleResult.setCodeType(CodeType.SYSTEM);
            getEntityManager().persist(singleResult);
            getEntityManager().flush();
        }

        log.info("Current database schema version is [" + singleResult.getValue() + "]");

    }

//    private void validateIdentityObjectTypes() {
//        if (getEntityManager().createQuery("select t from IdentityObjectType t where t.name = :name")
//                .setParameter("name", "USER")
//                .getResultList().isEmpty()) {
//
//            IdentityObjectType user = new IdentityObjectType();
//            user.setName("USER");
//            getEntityManager().persist(user);
//        }
//
//        if (getEntityManager().createQuery("select t from IdentityObjectType t where t.name = :name")
//                .setParameter("name", "GROUP")
//                .getResultList().isEmpty()) {
//
//            IdentityObjectType group = new IdentityObjectType();
//            group.setName("GROUP");
//            getEntityManager().persist(group);
//        }
//    }
    private void validateGroups() {

        Group singleResult = null;
        Map<String, String> props = new HashMap<>();

        //Grupos de arranque del sistema
        props.put("fede", "fede:1");
        props.put("salud", "Salud:2");
        props.put("alimentos", "Alimentos:3");
        props.put("ropa", "Ropa:4");
        props.put("educacion", "Educación:5");
        props.put("vivienda", "Vivienda:6");

        props.put("favorito", "Favoritos:7");

        String value = null;
        String name = null;
        Short orden = null;
        for (String key : props.keySet()) {
            value = props.get(key);
            try {
                TypedQuery<Group> query = getEntityManager().createQuery("from Group g where g.code='" + key + "'",
                        Group.class);
                singleResult = query.getSingleResult();
            } catch (NoResultException e) {

                java.util.Date now = Dates.now();
                name = value.split(":")[0];
                orden = Short.valueOf(value.split(":")[1]);
                singleResult = new Group(key, name);
                singleResult.setOrden(orden);
                singleResult.setCreatedOn(now);
                singleResult.setLastUpdate(now);
                singleResult.setCodeType(CodeType.TAG);
                singleResult.setStatus(StatusType.ACTIVE.toString());
                getEntityManager().persist(singleResult);
                getEntityManager().flush();
            }

            log.info("Added group code: {} name: [{}]", key, singleResult.getName());
        }
    }

    private void validateSetting() {

        Setting singleResult = null;
        Map<String, String> props = new HashMap<>();

        //email settings
//        props.put("mail.from", "fede@jlgranda.com:Remitente de correo electrónico");
//        props.put("mail.host", "jlgranda.com:Servidor de correo electrónico");
//        props.put("mail.user", "fede:Usuario de correo electrónico");
//        props.put("mail.password", "LieferQuireMidstUpends95: Contraseña de correo electrónico");
//        props.put("mail.inbox", "INBOX:Carpeta de correo electrónico");
//        props.put("mail.port", "25:Puerto de correo");
//        props.put("mail.verbose", "false:Mostrar detalles de conversación de sessión de correo");
//        props.put("mail.debug", "false:Mostrar información de depuración de sessión de correo");
//        
//        props.put("mail.store.protocol", "imap:Protocolo de sessión de correo");
//        props.put("mail.transport.protocol", "smtps:Protocolo seguro de sessión de correo");
        props.put("mail.imap.host", "jlgranda.com:Servidor IMAP:IMAP");
        props.put("mail.imap.port", "25:Puerto IMAP:IMAP");
        props.put("mail.imaps.ssl.trust", "*:SSL TRUST:SMTPS");

        props.put("mail.smtps.port", "25:Puerto SMTP:SMTPS");
        props.put("mail.smtps.host", "jlgranda.com:Servidor de envios SMTPS:SMTPS");
        props.put("mail.smtps.from", "AppsVentas <consiguemas@jlgranda.com>:Remitente de correo:SMTPS");
        props.put("mail.smtps.username", "consiguemas@jlgranda.com:SMTP Username:SMTPS");
        props.put("mail.smtps.password", "LitePorePrudePursed13:SMTP Password:SMTPS");
        props.put("mail.smtps.auth", "true:Usar autenticación:SMTPS");
        props.put("mail.smtps.ssl", "true:SSL:SMTPS");
        props.put("mail.smtp.starttls.enable", "false:Usar TTLS:SMTPS");

        //XML utils settings
        props.put("fede.xml.tag.numeroAutorizacion", "<numeroAutorizacion></numeroAutorizacion>:Etiqueta de número de autorización en XML:XML");
        props.put("fede.xml.tag.fechaAutorizacion", "<fechaAutorizacion></fechaAutorizacion>:Etiqueta de fecha de autorización en XML:XML");

        //dates patterns
        props.put("fede.date.pattern", "dd/MM/yyyy:Formato de fecha:SYSTEM");

        //UI settings
        props.put("fede.group.default", "fede:Grupo por defecto:UI");
        props.put("fede.dashboard.timeline.length", "10:Longitud de lista en dashboard::UI");
        props.put("fede.dashboard.range", "364:Rango de tiempo para mostrar tablas:UI"); //364 días
        props.put("fede.inbox.list.length", "50:Número de filas en detalles tipo tabla:UI"); //50 días
        props.put("app.fede.sales.dashboard.refreshtime", "20:Tiempo de refresco en pantallas de monitoreo:UI"); //20 segundos
        props.put("app.fede.sales.dashboard.lasts.list.length", "10:Longitud de listas rápidas:UI"); //10 segundos
        props.put("app.fede.sizeLimit", "1048576:Tamaño máximo de carga de archivo:UI"); //maximo tamaño de archivo a subir
        props.put("app.fede.fileLimit", "34:Número máximo de archivos a cargar:UI"); //Maximo de archivos a subir
        props.put("app.fede.allowTypes", "/(\\.|\\/)(xml|zip|pdf|txt)$/:Tipos de archivos permitidos:UI"); //Tipo de archivo permitidos
        props.put("app.management.tarea.documentos.sizeLimit", "1048576:Tamaño máximo de carga de archivo:UI"); //maximo tamaño de archivo a subir
        props.put("app.management.tarea.documentos.fileLimit", "34:Número máximo de archivos a cargar:UI"); //Maximo de archivos a subir
        props.put("app.management.tarea.documentos.allowTypes", "/(\\.|\\/)(xml|zip|pdf|txt)$/:Tipos de archivos permitidos:UI"); //Tipo de archivo permitidos
        props.put("app.admin.subject.sizeLimit", "1048576:Tamaño máximo de carga de archivo:UI"); //maximo tamaño de archivo a subir
        props.put("app.admin.subject.fileLimit", "34:Número máximo de archivos a cargar:UI"); //Maximo de archivos a subir
        props.put("app.admin.subject.allowTypes", "/(\\.|\\/)(gif|jpe?g|png)$/:Tipos de archivos permitidos:UI"); //Tipo de archivo permitidos
        props.put("app.profile.photo.width", "100:Ancho de Imagen:UI"); //Ancho de imágenes
        props.put("app.profile.photo.height", "100:Alto de Imagen:UI"); //Alto de imágenes
        
        props.put("app.fede.decimalSeparator", ".:Separador de decimales:CORE");
        props.put("app.fede.thousandSeparator", ",:Separador de miles:CORE");
        props.put("app.fede.decimalPlaces", "2:Número de digitos a mostrar en campos tipo número:CORE");
        props.put("app.fede.inventory.top", "10:Número de elementos en listas top:CORE");

        String label = null;
        String value = null;
        String category = null;
        for (String key : props.keySet()) {
            value = props.get(key).split(":")[0];
            label = props.get(key).split(":")[1];
            category = props.get(key).split(":")[2];
            try {
                TypedQuery<Setting> query = getEntityManager().createQuery("from Setting s where s.name='" + key + "' and owner is null",
                        Setting.class);
                singleResult = query.getSingleResult();
            } catch (NoResultException e) {
                java.util.Date now = Dates.now();
                singleResult = new Setting(label, key, value);
                singleResult.setCategory(category);
                singleResult.setCreatedOn(now);
                singleResult.setLastUpdate(now);
                singleResult.setCodeType(CodeType.SYSTEM);
                singleResult.setStatus(StatusType.ACTIVE.toString());
                getEntityManager().persist(singleResult);
                getEntityManager().flush();
            }

            log.info("Current  {} is [{}]", key, singleResult.getValue());
        }
    }

    private void validateSecurity() {
        Subject singleResult = null;
        try {
            TypedQuery<Subject> query = getEntityManager().createQuery("from Subject s where s.username='admin'",
                    Subject.class);
            singleResult = query.getSingleResult();
        } catch (NoResultException e) {
            singleResult = createAdministrator();
            getEntityManager().persist(singleResult);
        }
    }
    private void validateDefaultCustomer() {
        Subject singleResult = null;
        try {
            TypedQuery<Subject> query = getEntityManager().createQuery("from Subject s where s.username='consumidorfinal'",
                    Subject.class);
            singleResult = query.getSingleResult();
        } catch (NoResultException e) {
            singleResult = createDefaultCustomer();
            getEntityManager().persist(singleResult);
        }
    }
    private void validateDefaultSupplier() {
        Subject singleResult = null;
        try {
            TypedQuery<Subject> query = getEntityManager().createQuery("from Subject s where s.username='proveedorsinfactura'",
                    Subject.class);
            singleResult = query.getSingleResult();
        } catch (NoResultException e) {
            singleResult = createDefaultSupplier();
            getEntityManager().persist(singleResult);
        }
    }

    private Subject createAdministrator() {
        Subject singleResult = new Subject();
        singleResult.setEmail("admin@fede.com");
        singleResult.setUsername("admin");
        singleResult.setPassword((new org.apache.commons.codec.digest.Crypt().crypt("consumidorfinal")));
        singleResult.setUsernameConfirmed(true);
        singleResult.setCreatedOn(Dates.now());
        singleResult.setLastUpdate(Dates.now());
        singleResult.setCodeType(CodeType.NONE);
        singleResult.setSubjectType(Subject.Type.SYSTEM);

        getEntityManager().persist(singleResult);
        return singleResult;
    }
    private Subject createDefaultSupplier() {
        Subject singleResult = new Subject();
        singleResult.setEmail("proveedorsinfactura@fede.com");
        singleResult.setUsername("proveedorsinfactura");
        singleResult.setFirstname("Proveedor");
        singleResult.setSurname("Sin factura");
        singleResult.setCode("000000000000");
        singleResult.setCodeType(CodeType.RUC);
        singleResult.setPassword((new org.apache.commons.codec.digest.Crypt().crypt("pr0v33d0r")));
        singleResult.setUsernameConfirmed(true);
        singleResult.setCreatedOn(Dates.now());
        singleResult.setLastUpdate(Dates.now());
        singleResult.setSubjectType(Subject.Type.SYSTEM);

        getEntityManager().persist(singleResult);
        return singleResult;
    }
    private Subject createDefaultCustomer() {
        Subject singleResult = new Subject();
        singleResult.setEmail("consumidorfinal@fede.com");
        singleResult.setUsername("consumidorfinal");
        singleResult.setFirstname("Consumidor");
        singleResult.setSurname("Final");
        singleResult.setCode("9999999999999");
        singleResult.setCodeType(CodeType.RUC);
        singleResult.setPassword((new org.apache.commons.codec.digest.Crypt().crypt("c0nsum1d0rf1n4l")));
        singleResult.setUsernameConfirmed(true);
        singleResult.setCreatedOn(Dates.now());
        singleResult.setLastUpdate(Dates.now());
        singleResult.setSubjectType(Subject.Type.SYSTEM);

        getEntityManager().persist(singleResult);
        return singleResult;
    }

    private EntityManager getEntityManager() {
        return em;
    }
}
