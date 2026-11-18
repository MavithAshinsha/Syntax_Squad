package main.java.edu.eclass.Log_reg.model;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Graphical front end for the E-Class Portal.
 * Uses the same text files and backend classes as the console version.
 * Run: java -cp bin EclassGUI
 */
public class EclassGUI {

    // ---------- files ----------
    static final String STUDENT_FILE = "student.txt";
    static final String COURSES_FILE = "Courses.txt";
    static final String REG_FILE = "CourseRegistration.txt";
    static final String ATT_FILE = "attendence.txt";
    static final String NOTE_FILE = "parent_notifications.txt";
    static final String ADMIN_FILE = "admin.txt";

    // ---------- theme ----------
    static final Color PRIMARY = new Color(0x4F46E5);
    static final Color PRIMARY_DARK = new Color(0x3730A3);
    static final Color SIDEBAR = new Color(0x1E1B4B);
    static final Color SIDEBAR_HOVER = new Color(0x312E81);
    static final Color BG = new Color(0xF3F4F6);
    static final Color TEXT = new Color(0x111827);
    static final Color MUTED = new Color(0x6B7280);
    static final Color GREEN = new Color(0x16A34A);
    static final Color RED = new Color(0xDC2626);
    static final Color BORDER = new Color(0xD1D5DB);
    static final Font BASE = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font BOLD = BASE.deriveFont(Font.BOLD);

    // ---------- state ----------
    JFrame frame;
    CardLayout rootLayout = new CardLayout();
    JPanel root = new JPanel(rootLayout);
    JPanel sidebar;
    JPanel content;
    JLabel headerTitle;
    JLabel headerUser;
    boolean collapsed = false;
    boolean isAdmin;
    String userId;
    final List<NavItem> navItems = new ArrayList<>();
    Timer slideTimer;

    static class NavItem {
        String label, shortText;
        Supplier<JPanel> page;
        SideButton button;
        NavItem(String label, String shortText, Supplier<JPanel> page) {
            this.label = label; this.shortText = shortText; this.page = page;
        }
    }

    public static void main(String[] args) {
        new adminLogin(new Scanner(System.in)); // creates default admin.txt if missing
        SwingUtilities.invokeLater(() -> new EclassGUI().start());
    }

    // =====================================================================
    //  Start-up / window
    // =====================================================================
    void start() {
        for (Object key : java.util.Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(BASE));
            }
        }
        frame = new JFrame("E-Class Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);

        root.add(buildAuthScreen(), "auth");
        root.add(buildAppShell(), "app");
        frame.setContentPane(root);
        rootLayout.show(root, "auth");
        frame.setVisible(true);
    }

    // =====================================================================
    //  Auth screen (student login / register / admin login)
    // =====================================================================
    JPanel buildAuthScreen() {
        JPanel screen = new JPanel(new GridLayout(1, 2));

        // brand panel
        JPanel brand = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY, getWidth(), getHeight(), SIDEBAR));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillOval(-80, getHeight() - 220, 320, 320);
                g2.fillOval(getWidth() - 160, -80, 260, 260);
                g2.dispose();
            }
        };
        brand.setLayout(new GridBagLayout());
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel t1 = label("E-Class Portal", 36, Font.BOLD, Color.WHITE);
        JLabel t2 = label("Learn. Register. Attend.", 16, Font.PLAIN, new Color(0xC7D2FE));
        JLabel t3 = label("Manage courses, payments and attendance", 13, Font.PLAIN, new Color(0xA5B4FC));
        brandText.add(t1); brandText.add(Box.createVerticalStrut(8));
        brandText.add(t2); brandText.add(Box.createVerticalStrut(24)); brandText.add(t3);
        brand.add(brandText);

        // form side
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        JPanel box = new JPanel(new BorderLayout(0, 18));
        box.setOpaque(false);
        box.setPreferredSize(new Dimension(400, 560));

        CardLayout formLayout = new CardLayout();
        JPanel forms = new JPanel(formLayout);
        forms.setOpaque(false);
        forms.add(studentLoginForm(), "login");
        forms.add(registerForm(), "register");
        forms.add(adminLoginForm(), "admin");

        JPanel tabs = new JPanel(new GridLayout(1, 3, 6, 0));
        tabs.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        String[][] defs = {{"Student Login", "login"}, {"Register", "register"}, {"Admin", "admin"}};
        for (int i = 0; i < defs.length; i++) {
            final String key = defs[i][1];
            JToggleButton tb = new JToggleButton(defs[i][0]);
            tb.setFont(BOLD);
            tb.setFocusPainted(false);
            tb.setBorder(new EmptyBorder(9, 6, 9, 6));
            tb.setBackground(new Color(0xE5E7EB));
            tb.setForeground(TEXT);
            tb.addChangeListener(e -> {
                tb.setBackground(tb.isSelected() ? PRIMARY : new Color(0xE5E7EB));
                tb.setForeground(tb.isSelected() ? Color.WHITE : TEXT);
            });
            tb.addActionListener(e -> formLayout.show(forms, key));
            group.add(tb);
            tabs.add(tb);
            if (i == 0) tb.setSelected(true);
        }
        box.add(tabs, BorderLayout.NORTH);
        box.add(forms, BorderLayout.CENTER);
        right.add(box);

        screen.add(brand);
        screen.add(right);
        return screen;
    }

    JPanel studentLoginForm() {
        JPanel p = formPanel();
        JTextField id = field();
        JPasswordField pw = new JPasswordField();
        styleField(pw);
        JLabel err = errorLabel();
        int r = 0;
        addTitle(p, "Welcome back", "Sign in with your Student ID", r++);
        addRow(p, "Student ID", id, r++);
        addRow(p, "Password", pw, r++);
        p.add(err, gbc(0, r++, 2));
        JButton go = primaryButton("Login");
        p.add(go, gbc(0, r++, 2));
        glue(p, r);
        Runnable doLogin = () -> {
            String sid = id.getText().trim();
            if (sid.isEmpty() || pw.getPassword().length == 0) { err.setText("Enter your ID and password."); return; }
            if (logReg.checkID(sid, new String(pw.getPassword()))) {
                err.setText(" ");
                pw.setText("");
                openApp(sid, false);
            } else {
                err.setText("Invalid Student ID or password.");
            }
        };
        go.addActionListener(e -> doLogin.run());
        pw.addActionListener(e -> doLogin.run());
        id.addActionListener(e -> pw.requestFocus());
        return p;
    }

    JPanel registerForm() {
        JPanel p = formPanel();
        JTextField initials = field(), full = field(), birth = field(), email = field(),
                phone = field(), parent = field(), parentPhone = field();
        JPasswordField pw = new JPasswordField();
        styleField(pw);
        birth.setToolTipText("YYYY-MM-DD");
        JLabel err = errorLabel();
        int r = 0;
        addTitle(p, "Create account", "Register as a new student", r++);
        addRow(p, "Name with initials", initials, r++);
        addRow(p, "Full name", full, r++);
        addRow(p, "Birth date (YYYY-MM-DD)", birth, r++);
        addRow(p, "Password (min 6)", pw, r++);
        addRow(p, "Email", email, r++);
        addRow(p, "Phone", phone, r++);
        addRow(p, "Parent name", parent, r++);
        addRow(p, "Parent phone", parentPhone, r++);
        p.add(err, gbc(0, r++, 2));
        JButton go = primaryButton("Register");
        p.add(go, gbc(0, r++, 2));
        glue(p, r);

        go.addActionListener(e -> {
            String[] vals = {initials.getText().trim(), full.getText().trim(), birth.getText().trim(),
                    email.getText().trim(), phone.getText().trim(), parent.getText().trim(),
                    parentPhone.getText().trim()};
            String pass = new String(pw.getPassword());
            for (String v : vals) {
                if (v.isEmpty()) { err.setText("Please fill in every field."); return; }
                if (v.contains(",")) { err.setText("Commas are not allowed in any field."); return; }
            }
            if (pass.length() < 6 || pass.contains(",")) {
                err.setText("Password must be 6+ characters, no commas."); return;
            }
            try { LocalDate.parse(vals[2]); }
            catch (Exception ex) { err.setText("Birth date must look like 2005-07-10."); return; }
            if (!vals[4].matches("\\d{9,12}") || !vals[6].matches("\\d{9,12}")) {
                err.setText("Phone numbers must be 9-12 digits."); return;
            }
            if (!vals[3].contains("@")) { err.setText("Enter a valid email."); return; }

            logReg account = new logReg(new Scanner(System.in));
            String newId = String.format("ST%03d", account.getLastId());
            if (account.saveData(newId, vals[0], pass, vals[3], vals[4], vals[1], vals[2], vals[5], vals[6])) {
                err.setText(" ");
                JOptionPane.showMessageDialog(frame,
                        "Registration successful!\nYour Student ID is: " + newId + "\nPlease keep it safe.",
                        "Welcome", JOptionPane.INFORMATION_MESSAGE);
                for (JTextField f : new JTextField[]{initials, full, birth, email, phone, parent, parentPhone}) f.setText("");
                pw.setText("");
            } else {
                err.setText("Could not save your details.");
            }
        });
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(sp);
        return wrap;
    }

    JPanel adminLoginForm() {
        JPanel p = formPanel();
        JTextField id = field();
        JPasswordField pw = new JPasswordField();
        styleField(pw);
        JLabel err = errorLabel();
        int r = 0;
        addTitle(p, "Admin access", "Sign in to open the admin dashboard", r++);
        addRow(p, "Admin ID", id, r++);
        addRow(p, "Password", pw, r++);
        p.add(err, gbc(0, r++, 2));
        JButton go = primaryButton("Admin Login");
        p.add(go, gbc(0, r++, 2));
        glue(p, r);
        Runnable doLogin = () -> {
            String aid = id.getText().trim();
            if (adminLogin.checkAdmin(aid, new String(pw.getPassword()))) {
                err.setText(" ");
                pw.setText("");
                openApp(aid, true);
            } else {
                err.setText("Invalid admin ID or password.");
            }
        };
        go.addActionListener(e -> doLogin.run());
        pw.addActionListener(e -> doLogin.run());
        id.addActionListener(e -> pw.requestFocus());
        return p;
    }

    // =====================================================================
    //  App shell: sidebar + header + content
    // =====================================================================
    JPanel buildAppShell() {
        JPanel shell = new JPanel(new BorderLayout());
        sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(230, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, BORDER), new EmptyBorder(14, 22, 14, 22)));
        headerTitle = label("", 20, Font.BOLD, TEXT);
        headerUser = label("", 13, Font.PLAIN, MUTED);
        header.add(headerTitle, BorderLayout.WEST);
        header.add(headerUser, BorderLayout.EAST);

        content = new JPanel(new BorderLayout());
        content.setBackground(BG);

        JPanel main = new JPanel(new BorderLayout());
        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        shell.add(sidebar, BorderLayout.WEST);
        shell.add(main, BorderLayout.CENTER);
        return shell;
    }

    void openApp(String id, boolean admin) {
        this.userId = id;
        this.isAdmin = admin;
        navItems.clear();
        if (admin) {
            navItems.add(new NavItem("Dashboard", "D", this::adminDashboardPage));
            navItems.add(new NavItem("Students", "S", this::adminStudentsPage));
            navItems.add(new NavItem("Courses", "C", this::adminCoursesPage));
            navItems.add(new NavItem("Registrations", "R", this::adminRegistrationsPage));
            navItems.add(new NavItem("Attendance", "A", this::adminAttendancePage));
            navItems.add(new NavItem("Notifications", "N", this::adminNotificationsPage));
            navItems.add(new NavItem("Change Password", "P", this::adminPasswordPage));
        } else {
            navItems.add(new NavItem("My Profile", "P", this::profilePage));
            navItems.add(new NavItem("Courses", "C", this::coursesPage));
            navItems.add(new NavItem("Registrations & Payment", "$", this::paymentPage));
            navItems.add(new NavItem("Attendance", "A", this::attendancePage));
            navItems.add(new NavItem("Update Profile", "U", this::updateProfilePage));
        }
        collapsed = false;
        buildSidebar();
        headerUser.setText((admin ? "Admin: " : "Student: ") + id);
        select(navItems.get(0));
        rootLayout.show(root, "app");
    }

    void buildSidebar() {
        sidebar.removeAll();
        sidebar.setPreferredSize(new Dimension(230, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        JButton toggle = new JButton("\u2630");
        toggle.setFont(BASE.deriveFont(Font.BOLD, 20f));
        toggle.setForeground(Color.WHITE);
        toggle.setContentAreaFilled(false);
        toggle.setBorder(new EmptyBorder(14, 20, 14, 20));
        toggle.setFocusPainted(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setToolTipText("Collapse / expand menu");
        toggle.addActionListener(e -> slide());
        top.add(toggle, BorderLayout.WEST);
        sidebar.add(top);
        sidebar.add(Box.createVerticalStrut(8));

        for (NavItem item : navItems) {
            item.button = new SideButton(item.label, item.shortText);
            item.button.addActionListener(e -> select(item));
            sidebar.add(item.button);
            sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());
        SideButton out = new SideButton("Logout", "X");
        out.addActionListener(e -> logout());
        sidebar.add(out);
        sidebar.add(Box.createVerticalStrut(14));
        navItems.add(new NavItem("Logout", "X", null)); // placeholder so collapse updates it
        navItems.get(navItems.size() - 1).button = out;
        sidebar.revalidate();
        sidebar.repaint();
    }

    /** Slide the sidebar between wide and narrow. */
    void slide() {
        if (slideTimer != null && slideTimer.isRunning()) return;
        final int target = collapsed ? 230 : 68;
        collapsed = !collapsed;
        slideTimer = new Timer(10, null);
        slideTimer.addActionListener(e -> {
            int w = sidebar.getPreferredSize().width;
            int step = 22;
            w = (w < target) ? Math.min(target, w + step) : Math.max(target, w - step);
            sidebar.setPreferredSize(new Dimension(w, 0));
            boolean narrow = w < 140;
            for (NavItem it : navItems) if (it.button != null) it.button.setNarrow(narrow);
            sidebar.revalidate();
            sidebar.repaint();
            if (w == target) slideTimer.stop();
        });
        slideTimer.start();
    }

    void select(NavItem item) {
        for (NavItem it : navItems) if (it.button != null) it.button.setSelectedItem(it == item);
        headerTitle.setText(item.label);
        content.removeAll();
        content.add(item.page.get(), BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    void logout() {
        if (slideTimer != null) slideTimer.stop();
        userId = null;
        rootLayout.show(root, "auth");
    }

    /** Navigation button used in the sidebar. */
    static class SideButton extends JButton {
        final String full, shortText;
        boolean selected, hover;
        SideButton(String full, String shortText) {
            super(full);
            this.full = full; this.shortText = shortText;
            setFont(BOLD);
            setForeground(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(11, 24, 11, 12));
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        void setNarrow(boolean narrow) {
            setText(narrow ? shortText : full);
            setToolTipText(narrow ? full : null);
            setHorizontalAlignment(narrow ? SwingConstants.CENTER : SwingConstants.LEFT);
        }
        void setSelectedItem(boolean s) { selected = s; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected || hover) {
                g2.setColor(selected ? PRIMARY : SIDEBAR_HOVER);
                g2.fillRoundRect(8, 0, getWidth() - 16, getHeight(), 14, 14);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    //  STUDENT PAGES
    // =====================================================================
    JPanel profilePage() {
        String[] s = findStudent(userId);
        if (s == null) return page(new JLabel("Profile not found."));
        String[][] rows = {{"Student ID", s[0]}, {"Name", s[1]}, {"Full name", s[5]}, {"Birth date", s[6]},
                {"Email", s[3]}, {"Phone", s[4]}, {"Parent name", s[7]}, {"Parent phone", s[8]}};
        JPanel card = card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 30);
        c.anchor = GridBagConstraints.WEST;
        JLabel hi = label("Hello, " + s[1], 22, Font.BOLD, TEXT);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(hi, c);
        c.gridwidth = 1;
        for (int i = 0; i < rows.length; i++) {
            c.gridy = i + 1; c.gridx = 0;
            card.add(label(rows[i][0], 13, Font.PLAIN, MUTED), c);
            c.gridx = 1;
            card.add(label(rows[i][1], 15, Font.BOLD, TEXT), c);
        }
        return page(wrapTop(card));
    }

    JPanel coursesPage() {
        List<String[]> courses = readCourses();
        JTable table = makeTable(new String[]{"Course ID", "Course Name", "Fee (Rs.)"}, courses);
        JButton reg = primaryButton("Register for selected course");
        JLabel info = label("Select a course and press Register.", 13, Font.PLAIN, MUTED);
        reg.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Please select a course first."); return; }
            String cid = String.valueOf(table.getValueAt(row, 0));
            String cname = String.valueOf(table.getValueAt(row, 1));
            if (new Payment().checkRegistration(userId, cid)) {
                warn("You are already registered for " + cname + "."); return;
            }
            try (FileWriter w = new FileWriter(REG_FILE, true)) {
                w.write("Student ID: " + userId + System.lineSeparator());
                w.write("Course ID: " + cid + System.lineSeparator());
                w.write("-------------------" + System.lineSeparator());
                inform("Registered for " + cname + ".\nGo to 'Registrations & Payment' to pay the fee.");
            } catch (IOException ex) {
                warn("Could not save registration: " + ex.getMessage());
            }
        });
        JPanel south = new JPanel(new BorderLayout(12, 0));
        south.setOpaque(false);
        south.add(info, BorderLayout.CENTER);
        south.add(reg, BorderLayout.EAST);
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(south, BorderLayout.SOUTH);
        return page(body);
    }

    JPanel paymentPage() {
        List<String[]> mine = new ArrayList<>();
        for (String[] r : readRegistrations()) {
            if (r[0].equals(userId)) {
                String[] c = findCourse(r[1]);
                mine.add(new String[]{c == null ? r[1] : c[0], c == null ? "(unknown)" : c[1],
                        c == null ? "-" : c[2], "Successful".equalsIgnoreCase(r[2]) ? "Paid" : "Pending"});
            }
        }
        JTable table = makeTable(new String[]{"Course ID", "Course Name", "Fee (Rs.)", "Status"}, mine);
        JButton pay = primaryButton("Pay for selected course");
        JLabel info = label(mine.isEmpty() ? "You have no registrations yet. Register from the Courses page."
                : "Select a pending course to pay.", 13, Font.PLAIN, MUTED);
        pay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Please select a course first."); return; }
            String cid = String.valueOf(table.getValueAt(row, 0));
            String cname = String.valueOf(table.getValueAt(row, 1));
            String fee = String.valueOf(table.getValueAt(row, 2));
            if ("Paid".equals(table.getValueAt(row, 3))) { inform("This course is already paid."); return; }
            if (!showCardDialog(cname, fee)) return;
            if (new Payment().updatePayment(userId, cid, "Successful")) {
                inform("Payment successful for " + cname + ".");
                select(navItems.get(2));
            } else {
                warn("Payment could not be recorded.");
            }
        });
        JPanel south = new JPanel(new BorderLayout(12, 0));
        south.setOpaque(false);
        south.add(info, BorderLayout.CENTER);
        south.add(pay, BorderLayout.EAST);
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(south, BorderLayout.SOUTH);
        return page(body);
    }

    boolean showCardDialog(String courseName, String fee) {
        JTextField holder = field(), number = field(), expiry = field(), cvv = field();
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 6));
        p.add(label("Course: " + courseName + "   |   Fee: Rs. " + fee, 14, Font.BOLD, TEXT));
        p.add(new JLabel("Card holder name"));  p.add(holder);
        p.add(new JLabel("Card number"));       p.add(number);
        p.add(new JLabel("Expiry (MM/YY)"));    p.add(expiry);
        p.add(new JLabel("CVV"));               p.add(cvv);
        p.add(label("Demo only - no real payment is processed.", 12, Font.ITALIC, MUTED));
        while (true) {
            int res = JOptionPane.showConfirmDialog(frame, p, "Payment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return false;
            if (holder.getText().trim().isEmpty()) { warn("Enter the card holder name."); continue; }
            if (!number.getText().replace(" ", "").matches("\\d{12,19}")) { warn("Card number must be 12-19 digits."); continue; }
            if (!expiry.getText().trim().matches("(0[1-9]|1[0-2])/\\d{2}")) { warn("Expiry must look like 08/28."); continue; }
            if (!cvv.getText().trim().matches("\\d{3,4}")) { warn("CVV must be 3 or 4 digits."); continue; }
            return true;
        }
    }

    JPanel attendancePage() {
        // paid courses only
        List<String[]> paid = new ArrayList<>();
        Payment pay = new Payment();
        for (String[] r : readRegistrations()) {
            if (r[0].equals(userId) && "Successful".equalsIgnoreCase(r[2])) {
                String[] c = findCourse(r[1]);
                if (c != null && paid.stream().noneMatch(x -> x[0].equalsIgnoreCase(c[0]))) paid.add(c);
            }
        }
        JComboBox<String> combo = new JComboBox<>();
        for (String[] c : paid) combo.addItem(c[0] + " - " + c[1]);
        combo.setFont(BASE);
        JButton mark = primaryButton("Mark Attendance");
        mark.setEnabled(!paid.isEmpty());

        List<String[]> history = readAttendanceFor(userId);
        JTable table = makeTable(new String[]{"Class", "Date / Time"}, history);

        mark.addActionListener(e -> {
            int i = combo.getSelectedIndex();
            if (i < 0) return;
            String[] c = paid.get(i);
            if (!pay.checkRegistration(userId, c[0]) || !pay.hasSuccessfulPayment(userId, c[0])) {
                warn("You must be registered and have paid for this class."); return;
            }
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            try (FileWriter fw = new FileWriter(ATT_FILE, true)) {
                fw.write("Student ID: " + userId + ", Class ID: " + c[0] + ", Class Name: " + c[1]
                        + ", Date/Time: " + ts + System.lineSeparator());
            } catch (IOException ex) {
                warn("Could not save attendance: " + ex.getMessage()); return;
            }
            boolean notified = new ParentManagementSystem().notifyAttendance(userId, c[1]);
            inform("Attendance marked for " + c[1] + ".\n"
                    + (notified ? "Your parent has been notified." : "Parent notification could not be saved."));
            select(navItems.get(3));
        });

        JPanel top = card(new FlowLayout(FlowLayout.LEFT, 12, 8));
        top.add(label("Class:", 14, Font.BOLD, TEXT));
        combo.setPreferredSize(new Dimension(300, 34));
        top.add(combo);
        top.add(mark);
        if (paid.isEmpty()) top.add(label("No paid classes yet.", 13, Font.PLAIN, MUTED));

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(top, BorderLayout.NORTH);
        JPanel hist = new JPanel(new BorderLayout(0, 6));
        hist.setOpaque(false);
        hist.add(label("My attendance history", 16, Font.BOLD, TEXT), BorderLayout.NORTH);
        hist.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(hist, BorderLayout.CENTER);
        return page(body);
    }

    JPanel updateProfilePage() {
        String[] s = findStudent(userId);
        if (s == null) return page(new JLabel("Profile not found."));
        String[] names = {"Name with initials", "Email", "Phone", "Full name", "Birth date (YYYY-MM-DD)",
                "Parent name", "Parent phone"};
        int[] idx = {1, 3, 4, 5, 6, 7, 8};
        JTextField[] fields = new JTextField[names.length];
        JPanel card = formPanel();
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        int r = 0;
        addTitle(card, "Edit your details", "Leave a field as it is to keep the current value", r++);
        for (int i = 0; i < names.length; i++) {
            fields[i] = field();
            fields[i].setText(s[idx[i]]);
            addRow(card, names[i], fields[i], r++);
        }
        JPasswordField pw = new JPasswordField();
        styleField(pw);
        addRow(card, "New password (blank = unchanged)", pw, r++);
        JLabel err = errorLabel();
        card.add(err, gbc(0, r++, 2));
        JButton save = primaryButton("Save Changes");
        card.add(save, gbc(0, r++, 2));
        save.addActionListener(e -> {
            String[] updated = s.clone();
            for (int i = 0; i < names.length; i++) {
                String v = fields[i].getText().trim();
                if (v.isEmpty() || v.contains(",")) { err.setText(names[i] + " cannot be empty or contain commas."); return; }
                updated[idx[i]] = v;
            }
            try { LocalDate.parse(updated[6]); } catch (Exception ex) { err.setText("Birth date must look like 2005-07-10."); return; }
            String np = new String(pw.getPassword());
            if (!np.isEmpty()) {
                if (np.length() < 6 || np.contains(",")) { err.setText("Password must be 6+ characters, no commas."); return; }
                updated[2] = np;
            }
            try {
                saveStudent(userId, updated);
                err.setText(" ");
                inform("Profile updated successfully.");
                select(navItems.get(4));
            } catch (IOException ex) {
                err.setText("Could not save: " + ex.getMessage());
            }
        });
        JScrollPane sp = new JScrollPane(wrapTop(card));
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(sp);
        return page(w);
    }

    // =====================================================================
    //  ADMIN PAGES
    // =====================================================================
    JPanel adminDashboardPage() {
        int students = readStudents().size();
        int courses = readCourses().size();
        List<String[]> regs = readRegistrations();
        long paid = regs.stream().filter(r -> "Successful".equalsIgnoreCase(r[2])).count();
        int attendance = readLines(ATT_FILE).size();
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.add(statCard("Students", String.valueOf(students), PRIMARY));
        grid.add(statCard("Courses", String.valueOf(courses), new Color(0x0891B2)));
        grid.add(statCard("Registrations", String.valueOf(regs.size()), new Color(0xD97706)));
        grid.add(statCard("Paid", String.valueOf(paid), GREEN));
        grid.add(statCard("Pending payments", String.valueOf(regs.size() - paid), RED));
        grid.add(statCard("Attendance records", String.valueOf(attendance), new Color(0x7C3AED)));
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(grid, BorderLayout.NORTH);
        return page(w);
    }

    JPanel statCard(String title, String value, Color color) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(new CompoundBorder(new MatteBorder(0, 6, 0, 0, color), new EmptyBorder(20, 20, 20, 20)));
        c.add(label(value, 34, Font.BOLD, color), BorderLayout.CENTER);
        c.add(label(title, 14, Font.PLAIN, MUTED), BorderLayout.SOUTH);
        return c;
    }

    JPanel adminStudentsPage() {
        List<String[]> rows = new ArrayList<>();
        for (String[] s : readStudents()) rows.add(new String[]{s[0], s[1], s[5], s[6], s[3], s[4], s[7], s[8]});
        JTable table = makeTable(new String[]{"ID", "Name", "Full Name", "Birth Date", "Email", "Phone",
                "Parent", "Parent Phone"}, rows);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        JTextField search = field();
        search.setToolTipText("Search by ID, name, phone...");
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void f() {
                String t = search.getText().trim();
                sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(t)));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { f(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { f(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { f(); }
        });
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.add(label("Search:", 14, Font.BOLD, TEXT), BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);
        top.add(label("Total: " + rows.size(), 14, Font.BOLD, MUTED), BorderLayout.EAST);
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(top, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        return page(body);
    }

    JPanel adminCoursesPage() {
        JTable table = makeTable(new String[]{"Course ID", "Course Name", "Fee (Rs.)"}, readCourses());
        JTextField id = field(), name = field(), fee = field();
        JLabel err = errorLabel();
        JButton add = primaryButton("Add Course");
        JPanel form = card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0; form.add(label("Course ID", 13, Font.PLAIN, MUTED), c);
        c.gridx = 1; form.add(label("Course name", 13, Font.PLAIN, MUTED), c);
        c.gridx = 2; form.add(label("Fee", 13, Font.PLAIN, MUTED), c);
        c.gridy = 1;
        c.gridx = 0; c.weightx = 0.2; form.add(id, c);
        c.gridx = 1; c.weightx = 0.6; form.add(name, c);
        c.gridx = 2; c.weightx = 0.2; form.add(fee, c);
        c.gridx = 3; c.weightx = 0; form.add(add, c);
        c.gridy = 2; c.gridx = 0; c.gridwidth = 4; form.add(err, c);

        add.addActionListener(e -> {
            String cid = id.getText().trim(), cname = name.getText().trim(), ftxt = fee.getText().trim();
            if (cid.isEmpty() || cname.isEmpty() || cid.contains(",") || cname.contains(",")) {
                err.setText("ID and name cannot be empty or contain commas."); return;
            }
            double f;
            try { f = Double.parseDouble(ftxt); } catch (NumberFormatException ex) { err.setText("Invalid fee."); return; }
            if (f <= 0) { err.setText("Fee must be greater than 0."); return; }
            if (findCourse(cid) != null) { err.setText("A course with that ID already exists."); return; }
            String feeOut = (f == Math.floor(f)) ? String.valueOf((long) f) : String.valueOf(f);
            try {
                File file = new File(COURSES_FILE);
                String prefix = "";
                if (!file.exists() || file.length() == 0) {
                    prefix = "CourseID,CourseName,CourseFee" + System.lineSeparator();
                } else {
                    String all = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    if (!all.endsWith("\n") && !all.endsWith("\r")) prefix = System.lineSeparator();
                }
                Files.writeString(file.toPath(), prefix + cid + "," + cname + "," + feeOut + System.lineSeparator(),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                inform("Course added successfully!");
                select(navItems.get(2));
            } catch (IOException ex) {
                err.setText("Could not save course: " + ex.getMessage());
            }
        });
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(form, BorderLayout.SOUTH);
        return page(body);
    }

    JPanel adminRegistrationsPage() {
        List<String[]> rows = new ArrayList<>();
        for (String[] r : readRegistrations()) {
            String[] c = findCourse(r[1]);
            rows.add(new String[]{r[0], r[1], c == null ? "(unknown)" : c[1], c == null ? "-" : c[2],
                    "Successful".equalsIgnoreCase(r[2]) ? "Paid" : "Pending"});
        }
        JTable table = makeTable(new String[]{"Student ID", "Course ID", "Course", "Fee (Rs.)", "Payment"}, rows);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setForeground("Paid".equals(v) ? GREEN : RED);
                setFont(BOLD);
                return comp;
            }
        });
        return page(new JScrollPane(table));
    }

    JPanel adminAttendancePage() {
        return page(textViewer(readLines(ATT_FILE), "No attendance records yet."));
    }

    JPanel adminNotificationsPage() {
        return page(textViewer(readLines(NOTE_FILE), "No parent notifications yet."));
    }

    JPanel adminPasswordPage() {
        JPasswordField cur = new JPasswordField(), np = new JPasswordField(), cf = new JPasswordField();
        styleField(cur); styleField(np); styleField(cf);
        JLabel err = errorLabel();
        JPanel card = formPanel();
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        int r = 0;
        addTitle(card, "Change admin password", "Minimum 6 characters, no commas", r++);
        addRow(card, "Current password", cur, r++);
        addRow(card, "New password", np, r++);
        addRow(card, "Confirm new password", cf, r++);
        card.add(err, gbc(0, r++, 2));
        JButton save = primaryButton("Change Password");
        card.add(save, gbc(0, r++, 2));
        save.addActionListener(e -> {
            String c0 = new String(cur.getPassword()), n = new String(np.getPassword()), c2 = new String(cf.getPassword());
            if (!adminLogin.checkAdmin(userId, c0)) { err.setText("Current password is incorrect."); return; }
            if (n.length() < 6 || n.contains(",")) { err.setText("New password must be 6+ characters, no commas."); return; }
            if (!n.equals(c2)) { err.setText("Passwords do not match."); return; }
            List<String> out = new ArrayList<>();
            for (String line : readLines(ADMIN_FILE)) {
                String[] f = line.split(",", -1);
                if (f.length >= 3 && f[0].trim().equals(userId)) { f[2] = n; out.add(String.join(",", f)); }
                else out.add(line);
            }
            try (FileWriter w = new FileWriter(ADMIN_FILE)) {
                for (String l : out) w.write(l + System.lineSeparator());
                cur.setText(""); np.setText(""); cf.setText(""); err.setText(" ");
                inform("Password changed successfully.");
            } catch (IOException ex) {
                err.setText("Could not save: " + ex.getMessage());
            }
        });
        return page(wrapTop(card));
    }

    // =====================================================================
    //  File helpers
    // =====================================================================
    static List<String> readLines(String file) {
        try {
            File f = new File(file);
            if (!f.exists()) return new ArrayList<>();
            return new ArrayList<>(Files.readAllLines(f.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    static List<String[]> readStudents() {
        List<String[]> list = new ArrayList<>();
        for (String line : readLines(STUDENT_FILE)) {
            String[] f = line.split(",", -1);
            if (f.length >= 9) list.add(f);
        }
        return list;
    }

    static String[] findStudent(String id) {
        for (String[] s : readStudents()) if (s[0].trim().equals(id)) return s;
        return null;
    }

    static void saveStudent(String id, String[] updated) throws IOException {
        List<String> out = new ArrayList<>();
        boolean done = false;
        for (String line : readLines(STUDENT_FILE)) {
            String[] f = line.split(",", -1);
            if (!done && f.length >= 9 && f[0].trim().equals(id)) { out.add(String.join(",", updated)); done = true; }
            else out.add(line);
        }
        if (!done) throw new IOException("Student record was not found.");
        try (FileWriter w = new FileWriter(STUDENT_FILE)) {
            for (String l : out) w.write(l + System.lineSeparator());
        }
    }

    static List<String[]> readCourses() {
        List<String[]> list = new ArrayList<>();
        for (String line : readLines(COURSES_FILE)) {
            String[] f = line.split(",", -1);
            if (f.length == 3 && !line.startsWith("CourseID")) list.add(new String[]{f[0].trim(), f[1].trim(), f[2].trim()});
        }
        return list;
    }

    static String[] findCourse(String id) {
        for (String[] c : readCourses()) if (c[0].equalsIgnoreCase(id.trim())) return c;
        return null;
    }

    /** Returns {studentId, courseId, paymentStatus-or-empty} for every registration block. */
    static List<String[]> readRegistrations() {
        List<String[]> list = new ArrayList<>();
        String sid = null, cid = null, status = "";
        for (String raw : readLines(REG_FILE)) {
            String line = raw.trim();
            if (line.startsWith("Student ID: ")) {
                if (sid != null && cid != null) list.add(new String[]{sid, cid, status});
                sid = line.substring(12).trim(); cid = null; status = "";
            } else if (line.startsWith("Course ID: ")) {
                cid = line.substring(11).trim();
            } else if (line.startsWith("Payment Status: ")) {
                status = line.substring(16).trim();
            } else if (line.startsWith("----")) {
                if (sid != null && cid != null) list.add(new String[]{sid, cid, status});
                sid = null; cid = null; status = "";
            }
        }
        if (sid != null && cid != null) list.add(new String[]{sid, cid, status});
        return list;
    }

    static List<String[]> readAttendanceFor(String studentId) {
        Pattern p = Pattern.compile("Student ID:\\s*(\\S+?),\\s*Class ID:\\s*(\\S+?),(?:\\s*Class Name:\\s*(.+?),)?\\s*Date/Time:\\s*(.+)");
        List<String[]> rows = new ArrayList<>();
        for (String line : readLines(ATT_FILE)) {
            Matcher m = p.matcher(line.trim());
            if (m.matches() && m.group(1).equals(studentId)) {
                String name = m.group(3) != null ? m.group(3) : m.group(2);
                rows.add(new String[]{name, m.group(4).replace('T', ' ')});
            }
        }
        return rows;
    }

    // =====================================================================
    //  UI helpers
    // =====================================================================
    static JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(BASE.deriveFont(style, (float) size));
        l.setForeground(color);
        return l;
    }

    static JLabel errorLabel() {
        JLabel l = label(" ", 13, Font.PLAIN, RED);
        return l;
    }

    static JTextField field() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    static void styleField(JTextField f) {
        f.setFont(BASE);
        f.setPreferredSize(new Dimension(200, 36));
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(4, 10, 4, 10)));
    }

    static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = !isEnabled() ? new Color(0xA5B4FC) : getModel().isPressed() ? PRIMARY_DARK
                        : getModel().isRollover() ? new Color(0x4338CA) : PRIMARY;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(BOLD);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180, 40));
        return b;
    }

    static JPanel formPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        return p;
    }

    static GridBagConstraints gbc(int x, int y, int w) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y; c.gridwidth = w;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 4, 4, 4);
        return c;
    }

    static void addTitle(JPanel p, String title, String sub, int row) {
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(label(title, 24, Font.BOLD, TEXT));
        t.add(Box.createVerticalStrut(4));
        t.add(label(sub, 13, Font.PLAIN, MUTED));
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        p.add(t, gbc(0, row, 2));
    }

    static void addRow(JPanel p, String text, JComponent comp, int row) {
        JPanel line = new JPanel(new BorderLayout(0, 3));
        line.setOpaque(false);
        line.add(label(text, 13, Font.PLAIN, MUTED), BorderLayout.NORTH);
        line.add(comp, BorderLayout.CENTER);
        p.add(line, gbc(0, row, 2));
    }

    static void glue(JPanel p, int row) {
        GridBagConstraints c = gbc(0, row, 2);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        p.add(filler, c);
    }

    static JPanel card(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(0xE5E7EB), 1, true), new EmptyBorder(16, 18, 16, 18)));
        return p;
    }

    static JPanel page(JComponent body) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(22, 24, 22, 24));
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    static JPanel wrapTop(JComponent c) {
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(520, c.getPreferredSize().height));
        left.add(c);
        JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        flow.setOpaque(false);
        flow.add(left);
        w.add(flow, BorderLayout.NORTH);
        return w;
    }

    static JTable makeTable(String[] cols, List<String[]> rows) {
        DefaultTableModel model = new DefaultTableModel(rows.toArray(new Object[0][]), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setRowHeight(32);
        t.setFont(BASE);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xE0E7FF));
        t.setSelectionForeground(TEXT);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        JTableHeader h = t.getTableHeader();
        h.setFont(BOLD);
        h.setBackground(new Color(0xF9FAFB));
        h.setForeground(MUTED);
        h.setPreferredSize(new Dimension(0, 38));
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setBorder(new EmptyBorder(0, 10, 0, 10));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tb, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF9FAFB));
                return c;
            }
        });
        return t;
    }

    static JScrollPane textViewer(List<String> lines, String emptyText) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(12, 14, 12, 14));
        area.setText(lines.isEmpty() ? emptyText : String.join("\n", lines));
        area.setCaretPosition(0);
        return new JScrollPane(area);
    }

    void warn(String msg) { JOptionPane.showMessageDialog(frame, msg, "Notice", JOptionPane.WARNING_MESSAGE); }
    void inform(String msg) { JOptionPane.showMessageDialog(frame, msg, "E-Class", JOptionPane.INFORMATION_MESSAGE); }
}

