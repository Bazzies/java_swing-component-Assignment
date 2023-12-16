package SoftwareAssignmentFinal;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

class User{
    public String Id;
    public String Pw;
    public User(String id, String pw) {
        Id = id;
        Pw = pw;
    }
}
class UserList {
    ArrayList<User> ul = new ArrayList<>();
    public void add(User u) { ul.add(u); }
    public boolean isValidID(String i) { //등록된 아이디인지 검사
        for(User u : ul)
            if(u.Id.equals(i)) return true;
        return false;
    }
    public boolean isValidPass(String i, String p) { //해당 아이디의 암호가 정확한지 검사
        String pass = "";
        for(User u : ul)
            if(u.Id.equals(i)) {
                pass = u.Pw;
                break;
            }
        return pass.equals(p);
    }
}
class GValue {
    public static UserList cUserList;
}

class JoinWindow extends JFrame {
    JTextField tf;
    JPasswordField pf,pf1;
    public JoinWindow() {
        setTitle("회원 가입");
        setLocation(200, 400);
        setLayout(new GridLayout(4,1));

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(idPanel);

        JPanel pwPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(pwPanel);

        JPanel pwCheckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(pwCheckPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(buttonPanel);

        idPanel.add(new JLabel("아이디"));
        tf = new JTextField(20);
        idPanel.add(tf);
        JButton checkDuplicateButton = new JButton("중복 확인");
        idPanel.add(checkDuplicateButton);
        checkDuplicateButton.addActionListener(e -> {
            String inputId = tf.getText();
            if (GValue.cUserList.isValidID(inputId)) {
                JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "사용 가능한 아이디입니다.", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        pwPanel.add(new JLabel("비밀번호"));
        pf = new JPasswordField(20);
        pwPanel.add(pf);

        pwCheckPanel.add(new JLabel("비밀번호 확인"));
        pf1 = new JPasswordField(20);
        pwCheckPanel.add(pf1);

        JButton joinButton = getButton();
        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(joinButton);
        buttonPanel.add(cancelButton);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton getButton() {
        JButton joinButton = new JButton("가입");
        joinButton.addActionListener(e -> {
            String id = tf.getText();
            String pw = new String(pf.getPassword());
            String pw1 = new String(pf1.getPassword());

            if (GValue.cUserList.isValidID(id)) {
                JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다.", "가입 실패", JOptionPane.ERROR_MESSAGE);
            } else {
                if (!pw.equals(pw1)) {
                    JOptionPane.showMessageDialog(null, "암호 불일치", "가입 실패", JOptionPane.ERROR_MESSAGE);
                } else {
                    try (FileWriter fileWriter = new FileWriter("data.txt", true);
                         PrintWriter printWriter = new PrintWriter(fileWriter)) {
                        printWriter.println(id);
                        printWriter.println(pw);

                        User newUser = new User(id, pw);
                        GValue.cUserList.add(newUser);

                        JOptionPane.showMessageDialog(null, "가입이 완료되었습니다.", "가입 성공", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // 가입이 완료되면 회원등록 창 닫기

                        new loginWindow(); //로그인 화면 띄우기
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "파일에 쓰기 오류 발생", "가입 실패", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        return joinButton;
    }
}
class gameStart extends JFrame {
    public gameStart() {
        setTitle("게임 시작");
        setLocation(400, 400);
        setLayout(new GridLayout(3, 1));

        JPanel windowGameStart = new JPanel(); // 전체 컴포넌트 -> 좀 더 키워야함(눈에 잘 보일려면)
        windowGameStart.add(new JLabel("숫자 야구 게임"));

        JButton start = createStartButton();
        JButton gameCancel = new JButton("나가기"); // 나가기 버튼 컴포넌트(이것도 좀 키워야함)
        gameCancel.addActionListener(e -> dispose());

        add(windowGameStart);
        add(start);
        add(gameCancel);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JButton createStartButton() {
        JButton startButton = new JButton("게임 시작"); // 게임 시작 버튼 컴포넌트(이것도 좀 키워야함)
        startButton.addActionListener(e -> {
            new gameWindows();
            dispose();
        });
        return startButton;
    }

    private static class gameWindows extends JFrame {
        //전체 패널 구성 -> 왼쪽 패널(입력 창, 키 패드, OK버튼), 오른쪽 패널(사용자 ID, 텍스트 창), 아래 패드(종료)
        private final JTextField txt;
        private final JTextArea resultTextArea;

        public gameWindows() {
            // 전체 패널
            JPanel allPanel = new JPanel();
            allPanel.setLayout(new BorderLayout());
            //ALLPANEL.setLayout(new GridLayout(2, 1));

            // 왼쪽 패널 (키패드, 입력, OK 버튼)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BorderLayout());
            txt = new JTextField(20);
            leftPanel.add(txt, BorderLayout.NORTH);

            JPanel keyPanel = new JPanel();
            keyPanel.setLayout(new GridLayout(4, 3));
            for (int i = 1; i <=9; i++) {
                JButton btn = new JButton("" + i);
                btn.setPreferredSize(new Dimension(100, 30));
                btn.addActionListener(e -> txt.setText(txt.getText() + btn.getText()));
                keyPanel.add(btn);
            }
            //0추가
            JButton btn0 = new JButton("0");
            btn0.setPreferredSize(new Dimension(100, 30));
            btn0.addActionListener(e -> txt.setText(txt.getText() + btn0.getText()));

            // * 버튼 추가
            JButton btnAsterisk = new JButton("*");
            btnAsterisk.setPreferredSize(new Dimension(100, 30));
            btnAsterisk.addActionListener(e -> txt.setText(txt.getText() + btnAsterisk.getText()));

            // # 버튼 추가
            JButton btnSharp = new JButton("#");
            btnSharp.setPreferredSize(new Dimension(100, 30));
            btnSharp.addActionListener(e -> txt.setText(txt.getText() + btnSharp.getText()));

            keyPanel.add(btnAsterisk);
            keyPanel.add(btn0);
            keyPanel.add(btnSharp);

            JPanel okPanel = new JPanel();
            JButton okButton = new JButton("OK");
            okButton.setPreferredSize(new Dimension(300,30));
            okButton.addActionListener(e -> displayResult());
            okPanel.add(okButton,BorderLayout.SOUTH);

            leftPanel.add(keyPanel,BorderLayout.CENTER);
            leftPanel.add(okPanel,BorderLayout.SOUTH);

            // 오른쪽 패널 (사용자 ID, 결과 텍스트)
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout());
            JLabel userIdLabel = new JLabel("사용자 ID: " + loginWindow.tf.getText());

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BorderLayout());
            resultTextArea = new JTextArea(3, 15);
            resultTextArea.setEditable(false);
            textPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

            rightPanel.add(userIdLabel,BorderLayout.NORTH);
            rightPanel.add(textPanel, BorderLayout.CENTER);

            // 아래 패널 (종료 버튼)
            JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // FlowLayout으로 변경
            JButton exitButton = new JButton("종료");
            exitButton.setPreferredSize(new Dimension(500, 40)); // 버튼 크기 조절
            exitButton.addActionListener(e -> dispose());
            exitPanel.add(exitButton);


            //전체 패널
            allPanel.add(leftPanel, BorderLayout.WEST);
            allPanel.add(rightPanel, BorderLayout.EAST);
            allPanel.add(exitPanel, BorderLayout.SOUTH);

            // JFrame 설정
            add(allPanel);
            pack();
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private void displayResult() {
            // 입력된 값을 가져와서 텍스트 창에 표시하는 로직 구현
            String inputValue = txt.getText();
            resultTextArea.append(inputValue + "\n");
            // 추가로 할 일이 있다면 여기에 구현
        }
    }
}
class loginWindow extends JFrame {
    static JTextField tf;
    JPasswordField pf;
    JoinWindow jw;
    public loginWindow() {
        setTitle("로그인");
        setLocation(200,200);
        setLayout(new GridLayout(3, 1));

        JPanel p1 = new JPanel();
        add(p1);
        JPanel p2 = new JPanel();
        add(p2);
        JPanel p3 = new JPanel();
        add(p3);

        p1.add(new JLabel("아이디"));
        tf = new JTextField(20);
        p1.add(tf);

        p2.add(new JLabel("암 호"));
        pf = new JPasswordField(20);
        p2.add(pf);

        JButton login = new JButton("로그인");
        p3.add(login);
        login.addActionListener(e -> {
            String id = tf.getText();
            String pass = new String(pf.getPassword());
            if (!GValue.cUserList.isValidID(id))
                JOptionPane.showMessageDialog(null, "미등록 아이디", "로그인 정보", JOptionPane.ERROR_MESSAGE);
            else if (!GValue.cUserList.isValidPass(id, pass))
                JOptionPane.showMessageDialog(null, "부정확한 암호", "로그인 정보", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(null, "로그인 완료", "로그인 정보", JOptionPane.INFORMATION_MESSAGE);
                new gameStart();
                //게임 패드(숫자 맞추기)
                dispose();
            }
        });
        JButton reg = new JButton("회원가입");
        p3.add(reg);
        reg.addActionListener(e -> {
            jw = new JoinWindow();
            dispose();
        });

        JButton cancel = new JButton("취소");
        cancel.addActionListener(e -> dispose());
        p3.add(cancel);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class SoftWareAssignment{
    public static void main(String[] args) throws IOException {
        User u;
        UserList ul = new UserList();
        loginWindow lw = new loginWindow();
        Scanner sc = new Scanner(new File("data.txt"));
        while (sc.hasNext()) {
            String id = sc.next();
            String pass = sc.next();
            u = new User(id, pass);
            ul.add(u);
        }
        GValue.cUserList = ul;
        sc.close();
    }
}

//할일
//파일이 없을 떄 자동 생성하도록 하기
//야구 게임 구현하기
//야구 게임 환경 구현하기 - clear
//로그인 - clear
//회원 가입 - clear