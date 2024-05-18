package com.bravotic.nntp.protocol;

public class Handler {
    private final GroupsDatabase database;

    private Group selectedGroup;

    public Handler(GroupsDatabase db) {
        this.database = db;
    }
    public String generic() {
        return "200 Welcome to Snoozenet\r\n";
    }
    public String list(String keyword) {
        StringBuilder b = new StringBuilder();

        b.append("215 Information follows (multi-line)\r\n");

        Group[] groups = database.list();

        for (Group group : groups) {
            b.append(group.getName());
            b.append(" ");

            b.append(group.getLast());
            b.append(" ");

            b.append(group.getFirst());
            b.append(" ");

            if (group.isPostingAllowed()) {
                b.append("y");
            }
            else {
                b.append("n");
            }

            b.append("\r\n");
        }

        b.append(".\r\n");

        return b.toString();
    }

    public String group(String name) {
        Group selected = database.selectGroup(name);

        if (selected == null) {
            return "411 no such news group\r\n";
        }
        else {
            this.selectedGroup = selected;

            return "211 "
                    + selected.getTotal() + " "
                    + selected.getFirst() + " "
                    + selected.getLast() + " "
                    + selected.getName() + "\r\n";
        }
    }

    public String post(String content) {
        Article a = new Article(content);

        // FIXME: This will not work if multiple newsgroups are mentioned
        Group g = database.selectGroup(a.getHeader("Newsgroups"));
        if (g.isPostingAllowed() && g.post(content)) {
            return "240 article posted ok\r\n";
        }
        else if (!g.isPostingAllowed()) {
            return "440 posting not allowed\r\n";
        }
        else {
            return "441 posting failed\r\n";
        }
    }

    public String post() {
        return "340 send article to be posted. End with <CR-LF>.<CR-LF>\r\n";
    }

    public String head(int id) {
        if (selectedGroup != null) {
            Article a = selectedGroup.article(id);
            if (a != null) {
                return "221 "
                        + id
                        + " <" + a.getHeader("Message-ID")
                        + "> article retrieved - head follows\r\n"
                        + a.getHead()
                        + ".\r\n";
            }
            else {
                return "430 no such article found\r\n";
            }
        }
        else {
            return "412 no newsgroup has been selected\r\n";
        }
    }

    public String article(int id) {
        if (selectedGroup != null) {
            Article a = selectedGroup.article(id);
            if (a != null) {
                return "220 "
                        + id
                        + a.getHeader("Message-ID")
                        + " article retrieved - head and body follow\r\n"
                        + a.getArticle()
                        + ".\r\n";
            }
            else {
                return "430 no such article found\r\n";
            }
        }
        else {
            return "412 no newsgroup has been selected\r\n";
        }
    }

    public String xover() {
        StringBuilder resp = new StringBuilder();
        resp.append("224 Information overview follows\r\n");

        // FIXME: Bodge fix
        int start = this.selectedGroup.getFirst();
        int end = this.selectedGroup.getLast();
        for (int i = start; i <= end; i++) {
            Article a = this.selectedGroup.article(i);
            resp.append(i);
            resp.append("\t");
            resp.append(a.getHeader("Subject"));
            resp.append("\t");
            resp.append(a.getHeader("From"));
            resp.append("\t");
            resp.append(a.getHeader("Date"));
            resp.append("\t");
            resp.append(a.getHeader("Message-ID"));
            resp.append("\t");
            resp.append(a.getHeader("References").replace("\r\n", ""));
            resp.append("\t");
            resp.append(a.getHeader("Bytes"));
            resp.append("\t");
            resp.append(a.getHeader("Lines"));
            resp.append("\t");
            resp.append(a.getHeader("Xref"));
            resp.append("\r\n");
        }
        resp.append(".\r\n");
        return resp.toString();
    }

}
