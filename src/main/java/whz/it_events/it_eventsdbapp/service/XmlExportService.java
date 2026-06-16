package whz.it_events.it_eventsdbapp.service;

import jakarta.persistence.EntityManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exportiert alle relevanten Daten der IT-Events-Datenbank in eine XML-Datei.
 * Wird für die Praesentation (Pflichtanforderung Prof. Franke) benoetigt.
 */
public class XmlExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void exportToFile(File file) throws Exception {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("ITEventsExport");
            root.setAttribute("exportDate", java.time.LocalDateTime.now().format(FMT));
            doc.appendChild(root);

            exportLocations(doc, root, em);
            exportEvents(doc, root, em);
            exportTracks(doc, root, em);
            exportUsers(doc, root, em);
            exportTeams(doc, root, em);
            exportMembers(doc, root, em);
            exportJuries(doc, root, em);
            exportSubmissions(doc, root, em);
            exportScores(doc, root, em);
            exportSponsors(doc, root, em);
            exportSessions(doc, root, em);
            exportSpeakers(doc, root, em);

            writeToFile(doc, file);
        } finally {
            em.close();
        }
    }

    private void exportLocations(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Locations");
        root.appendChild(section);
        List<Location> list = em.createQuery("from location", Location.class).getResultList();
        for (Location l : list) {
            Element e = doc.createElement("Location");
            e.setAttribute("id", str(l.getId()));
            addChild(doc, e, "Name", l.getLocationName());
            addChild(doc, e, "Stadt", l.getStadt());
            addChild(doc, e, "Adresse", l.getAddress());
            section.appendChild(e);
        }
    }

    private void exportEvents(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Events");
        root.appendChild(section);
        List<Event> list = em.createQuery("from event", Event.class).getResultList();
        for (Event ev : list) {
            Element e = doc.createElement("Event");
            e.setAttribute("id", str(ev.getId()));
            addChild(doc, e, "Name", ev.getName());
            addChild(doc, e, "Beschreibung", ev.getDescription());
            addChild(doc, e, "Start", fmt(ev.getStartDate()));
            addChild(doc, e, "Ende", fmt(ev.getEndDate()));
            addChild(doc, e, "Status", ev.getStatus() != null ? ev.getStatus().name() : "");
            addChild(doc, e, "LocationId", ev.getLocation() != null ? str(ev.getLocation().getId()) : "");
            section.appendChild(e);
        }
    }

    private void exportTracks(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Tracks");
        root.appendChild(section);
        List<Track> list = em.createQuery("from track", Track.class).getResultList();
        for (Track t : list) {
            Element e = doc.createElement("Track");
            e.setAttribute("id", str(t.getId()));
            addChild(doc, e, "Name", t.getName());
            addChild(doc, e, "Beschreibung", t.getDescription());
            addChild(doc, e, "Deadline", fmt(t.getDeadlineDate()));
            addChild(doc, e, "EventId", t.getEvent() != null ? str(t.getEvent().getId()) : "");
            section.appendChild(e);
        }
    }

    private void exportUsers(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Users");
        root.appendChild(section);
        List<User> list = em.createQuery("from appUser", User.class).getResultList();
        for (User u : list) {
            Element e = doc.createElement("User");
            e.setAttribute("id", str(u.getId()));
            addChild(doc, e, "Name", u.getName());
            addChild(doc, e, "Nachname", u.getLastname());
            addChild(doc, e, "Email", u.getEmail());
            addChild(doc, e, "Rolle", u.getRole() != null ? u.getRole().name() : "");
            // Passwort wird aus Sicherheitsgruenden NICHT exportiert
            section.appendChild(e);
        }
    }

    private void exportTeams(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Teams");
        root.appendChild(section);
        List<Team> list = em.createQuery("from team", Team.class).getResultList();
        for (Team t : list) {
            Element e = doc.createElement("Team");
            e.setAttribute("id", str(t.getId()));
            addChild(doc, e, "Name", t.getName());
            addChild(doc, e, "ScoreValue", String.valueOf(t.getScoreValue()));
            addChild(doc, e, "Anmeldedatum", fmt(t.getRegistrationDate()));
            addChild(doc, e, "TrackId", t.getTrack() != null ? str(t.getTrack().getId()) : "");
            section.appendChild(e);
        }
    }

    private void exportMembers(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Members");
        root.appendChild(section);
        List<Member> list = em.createQuery("from member", Member.class).getResultList();
        for (Member m : list) {
            Element e = doc.createElement("Member");
            e.setAttribute("id", str(m.getId()));
            addChild(doc, e, "TeamId", m.getTeam() != null ? str(m.getTeam().getId()) : "");
            addChild(doc, e, "UserId", m.getUser() != null ? str(m.getUser().getId()) : "");
            addChild(doc, e, "TeamRolle", m.getTeamRole());
            section.appendChild(e);
        }
    }

    private void exportJuries(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Juries");
        root.appendChild(section);
        List<Jury> list = em.createQuery("from jury", Jury.class).getResultList();
        for (Jury j : list) {
            Element e = doc.createElement("Jury");
            e.setAttribute("id", str(j.getId()));
            addChild(doc, e, "UserId", j.getUser() != null ? str(j.getUser().getId()) : "");
            addChild(doc, e, "TrackId", j.getTrack() != null ? str(j.getTrack().getId()) : "");
            addChild(doc, e, "Fachgebiet", j.getProfArea());
            addChild(doc, e, "Info", j.getInfo());
            section.appendChild(e);
        }
    }

    private void exportSubmissions(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Submissions");
        root.appendChild(section);
        List<Submission> list = em.createQuery("from submission", Submission.class).getResultList();
        for (Submission s : list) {
            Element e = doc.createElement("Submission");
            e.setAttribute("id", str(s.getId()));
            addChild(doc, e, "Titel", s.getTitel());
            addChild(doc, e, "Kommentar", s.getComment());
            addChild(doc, e, "Status", s.getStatus() != null ? s.getStatus().name() : "");
            addChild(doc, e, "Typ", s.getParticipationType() != null ? s.getParticipationType().name() : "");
            addChild(doc, e, "Zeitpunkt", fmt(s.getSubmissionTime()));
            if (s instanceof SubmissionTeam st) {
                addChild(doc, e, "GitUrl", st.getGitUrl());
                addChild(doc, e, "DemoUrl", st.getDemoUrl());
                addChild(doc, e, "TeamId", st.getTeam() != null ? str(st.getTeam().getId()) : "");
            }
            section.appendChild(e);
        }
    }

    private void exportScores(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Scores");
        root.appendChild(section);
        List<Score> list = em.createQuery("from score", Score.class).getResultList();
        for (Score s : list) {
            Element e = doc.createElement("Score");
            e.setAttribute("id", str(s.getId()));
            addChild(doc, e, "Kriterium", s.getCriteria());
            addChild(doc, e, "Wert", s.getScoreValue() != null ? String.valueOf(s.getScoreValue()) : "");
            addChild(doc, e, "Kommentar", s.getComment());
            addChild(doc, e, "Datum", fmt(s.getReviewDate()));
            addChild(doc, e, "SubmissionId", s.getSubmission() != null ? str(s.getSubmission().getId()) : "");
            addChild(doc, e, "JuryId", s.getJury() != null ? str(s.getJury().getId()) : "");
            section.appendChild(e);
        }
    }

    private void exportSponsors(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Sponsors");
        root.appendChild(section);
        List<Sponsor> list = em.createQuery("from sponsor", Sponsor.class).getResultList();
        for (Sponsor s : list) {
            Element e = doc.createElement("Sponsor");
            e.setAttribute("id", str(s.getId()));
            addChild(doc, e, "Name", s.getName());
            addChild(doc, e, "Kontakt", s.getContact());
            section.appendChild(e);
        }
    }

    private void exportSessions(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Sessions");
        root.appendChild(section);
        List<Session> list = em.createQuery("from session", Session.class).getResultList();
        for (Session s : list) {
            Element e = doc.createElement("Session");
            e.setAttribute("id", str(s.getId()));
            addChild(doc, e, "Titel", s.getTitel());
            addChild(doc, e, "Beschreibung", s.getDescription());
            addChild(doc, e, "Start", fmt(s.getStartDate()));
            addChild(doc, e, "Ende", fmt(s.getEndDate()));
            addChild(doc, e, "Raum", s.getRoom());
            addChild(doc, e, "Kapazitaet", String.valueOf(s.getCapacity()));
            addChild(doc, e, "Typ", s.getSessionType() != null ? s.getSessionType().name() : "");
            addChild(doc, e, "EventId", s.getEvent() != null ? str(s.getEvent().getId()) : "");
            section.appendChild(e);
        }
    }

    private void exportSpeakers(Document doc, Element root, EntityManager em) {
        Element section = doc.createElement("Speakers");
        root.appendChild(section);
        List<Speaker> list = em.createQuery("from speaker", Speaker.class).getResultList();
        for (Speaker s : list) {
            Element e = doc.createElement("Speaker");
            e.setAttribute("id", str(s.getId()));
            addChild(doc, e, "Name", s.getName());
            addChild(doc, e, "Kontakt", s.getContact());
            section.appendChild(e);
        }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────

    private void addChild(Document doc, Element parent, String tag, String value) {
        Element child = doc.createElement(tag);
        child.setTextContent(value != null ? value : "");
        parent.appendChild(child);
    }

    private String str(Long id) {
        return id != null ? String.valueOf(id) : "";
    }

    private String fmt(java.time.LocalDateTime dt) {
        return dt != null ? dt.format(FMT) : "";
    }

    private void writeToFile(Document doc, File file) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }
}
