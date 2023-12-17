package SoftwareAssignmentFinal;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
class gameStartWindow extends JFrame {
    public gameStartWindow() {
        setTitle("게임 시작");
        setLocation(400, 300);

        JPanel gameStartPanel = new JPanel(new GridLayout(2, 1));
        gameStartPanel.setPreferredSize(new Dimension(400,400));

        JPanel windowGameTitlePanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("숫자 야구 게임");
        Font titleFont = titleLabel.getFont();
        titleLabel.setFont(new Font(titleFont.getName(), Font.PLAIN, 24)); // 글꼴 크기 조정
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        windowGameTitlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel windowGameButtonPanel = new JPanel(new BorderLayout());
        JButton startButton = createStartButton();
        startButton.setPreferredSize(new Dimension(200,100));

        JButton gameCancelButton = new JButton("나가기");
        gameCancelButton.addActionListener(e -> dispose());
        gameCancelButton.setPreferredSize(new Dimension(200,100));

        windowGameButtonPanel.add(startButton, BorderLayout.NORTH);
        windowGameButtonPanel.add(gameCancelButton, BorderLayout.SOUTH);

        gameStartPanel.add(windowGameTitlePanel);
        gameStartPanel.add(windowGameButtonPanel);

        add(gameStartPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JButton createStartButton() {
        JButton startButton = new JButton("게임 시작");
        startButton.addActionListener(e -> {
            new gameWindows();
            dispose();
        });
        return startButton;
    }
    private static class gameWindows extends JFrame {

        //게임을 위한 4자리 랜덤
        Random random = new Random();

        //전체 패널 구성 -> 왼쪽 패널(입력 창, 키 패드, OK버튼), 오른쪽 패널(사용자 ID, 텍스트 창), 아래 패드(종료)
        private final JTextField txt;
        private final JTextArea resultTextArea;
        private int okButtonClick = 0;
        private final JLabel gameCount;
        //0 ~ 9999번까지
        private final String baseBallGame = String.format("%04d", random.nextInt(1000));

        public gameWindows() {

            // 전체 패널
            JPanel allPanel = new JPanel();
            allPanel.setLayout(new BorderLayout());

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
            okButton.addActionListener(e -> {
                if(txt.getText().length() < 5)
                {
                    okButtonClick++;
                }
                displayOkButtonClickCount();
                displayResult();
                txt.setText("");
            });
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

            resultTextArea.append(baseBallGame + "\n"); //잠깐 출력해 놓은 것------>나중에 삭제
            textPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

            gameCount = new JLabel("시도 횟수 : " + okButtonClick);

            rightPanel.add(userIdLabel,BorderLayout.NORTH);
            rightPanel.add(textPanel, BorderLayout.CENTER);
            rightPanel.add(gameCount,BorderLayout.SOUTH);

            // 아래 패널 (종료 버튼)
            JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // FlowLayout으로 변경
            JButton exitButton = new JButton("종료");
            exitButton.setPreferredSize(new Dimension(500, 40)); // 버튼 크기 조절
            exitButton.addActionListener(e -> dispose());
            //게임을 포기했을 때 출력됨
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

        private void displayOkButtonClickCount(){
            gameCount.setText("시도 횟수 : " + okButtonClick);
        }
        private void displayResult() {
            // 입력된 값을 가져와서 텍스트 창에 표시하는 로직 구현
            gameLogic();
        }

        private void gameLogic() {
            //game logic
            String inputValue = txt.getText();
            if(inputValue.length() > 4) {
                JOptionPane.showMessageDialog(null,"숫자를 4개만 입력해주세요","숫자 초과",JOptionPane.ERROR_MESSAGE);
            }
            else{
                int strike, ball;
                strike = 0;
                ball = 0;
                for (int i = 0; i < inputValue.length(); i++) {
                    char current = inputValue.charAt(i);
                    char target = baseBallGame.charAt(i);

                    if (current == target) {
                        strike++;
                    } else {
                        for (int j = 0; j < inputValue.length(); j++){
                            char temp_current = inputValue.charAt(j);
                            if (target == temp_current){
                                ball++;
                            }
                        }
                    }
                }
                resultTextArea.append(inputValue + " " + strike + "S" + ball + "B\n");
                if (strike == 4) {
                    JOptionPane.showMessageDialog(null, "시도 횟수 :" + okButtonClick  + "\n축하합니다!!", "게임 완료", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
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
                new gameStartWindow();
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

        File file = new File("data.txt");
        try {
            if (file.createNewFile()) {
                System.out.println("파일이 생성되었습니다.");
            } else {
                System.out.println("파일이 이미 존재합니다.");
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger(SoftWareAssignment.class.getName());
            logger.log(Level.SEVERE, "파일 생성 중 오류가 발생했습니다", e);
        }
        Scanner sc = new Scanner(file);

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
//야구 게임 구현하기 -clear
//야구 게임 환경 구현하기 - clear
//로그인 - clear
//회원 가입 - clear