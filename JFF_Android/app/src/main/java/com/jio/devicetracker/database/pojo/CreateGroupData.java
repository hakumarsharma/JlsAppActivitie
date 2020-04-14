package com.jio.devicetracker.database.pojo;

import java.io.Serializable;
import java.util.List;

public class CreateGroupData implements Serializable {
    private String name;
    private Session session;
    private List<Consents> consents;

    public List<Consents> getConsents() {
        return consents;
    }

    public void setConsents(List<Consents> consents) {
        this.consents = consents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public class Session {
        private String from;
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }

    public class Consents {
        private String phone;
        private List<String> entities;
        private Session session;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public List<String> getEntities() {
            return entities;
        }

        public void setEntities(List<String> entities) {
            this.entities = entities;
        }

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }
    }
}
