import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeatReservationApp {
    private JFrame frame;
    private Seat[][] seats = new Seat[10][4];

    @SuppressWarnings("unchecked")
    private List<Seat>[][] additionLogs = (List<Seat>[][]) new ArrayList[10][4];

    private JLabel totalLabel = new JLabel("총 인원: 0");
    private JLabel totalCostLabel = new JLabel("총 금액: 0원");
    private static final int COST_PER_PERSON = 5000; // 1인당 5000원

    
    public void createMainMenu() {
        frame = new JFrame("좌석 예약 시스템");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 상단에 총 인원과 총 금액 표시 패널 제거하고 간단한 제목만 표시
        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder("좌석 예약 시스템 - 1인당 5,000원"));
        JLabel instructionLabel = new JLabel("각 좌석별로 인원수 × 5,000원이 계산됩니다");
        instructionLabel.setFont(new Font("돋움", Font.PLAIN, 14));
        titlePanel.add(instructionLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // 좌석 그리드 패널
        JPanel seatPanel = new JPanel(new GridLayout(10, 4, 10, 10));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 4; col++) {
                additionLogs[row][col] = new ArrayList<>();
                String seatLabel = (char) ('A' + col) + Integer.toString(10 - row);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createRaisedBevelBorder());

                JButton infoButton = new JButton();
                updateInfoButton(infoButton, row, col, seatLabel);

                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton actionButton = new JButton();
                JButton extendButton = new JButton("+1시간");
                JButton editButton = new JButton("추가");
                JButton modifyButton = new JButton("수정"); 
                

                final int r = row;
                final int c = col;

                if (seats[r][c] != null && seats[r][c].isReserved()) {
                    actionButton.setText("퇴실");
                    extendButton.setVisible(true);
                    editButton.setVisible(true);
                    modifyButton.setVisible(true);
                } else {
                    actionButton.setText("입실");
                    extendButton.setVisible(false);
                    editButton.setVisible(false);
                    modifyButton.setVisible(false);
                }

                actionButton.addActionListener(e -> {
                    if (seats[r][c] != null && seats[r][c].isReserved()) {
                        int res = JOptionPane.showConfirmDialog(frame, "이 좌석에서 퇴실하시겠습니까?", "퇴실 확인", JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            seats[r][c].cancel();
                            additionLogs[r][c].clear();
                            updateInfoButton(infoButton, r, c, seatLabel);
                            actionButton.setText("입실");
                            extendButton.setVisible(false);
                            editButton.setVisible(false);
                            modifyButton.setVisible(false);
                        }
                    } else {
                        JTextField peopleField = new JTextField();
                        Object[] message = {
                                "인원 수:", peopleField
                        };

                        int option = JOptionPane.showConfirmDialog(frame, message, "좌석 입장", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            String peopleStr = peopleField.getText().trim();
                            if (peopleStr.isEmpty()) {
                                JOptionPane.showMessageDialog(frame, "인원 수를 입력하세요.");
                                return;
                            }
                            try {
                                int people = Integer.parseInt(peopleStr);
                                if (people <= 0) {
                                    JOptionPane.showMessageDialog(frame, "인원 수는 1명 이상이어야 합니다.");
                                    return;
                                }
                                seats[r][c] = new Seat(people);
                                additionLogs[r][c].clear();
                                updateInfoButton(infoButton, r, c, seatLabel);
                                actionButton.setText("퇴실");
                                extendButton.setVisible(true);
                                editButton.setVisible(true);
                                modifyButton.setVisible(true);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(frame, "인원 수는 숫자여야 합니다.");
                            }
                        }
                    }
                });

                extendButton.addActionListener(e -> {
                    if (seats[r][c] != null && seats[r][c].isReserved()) {
                        seats[r][c].extendTime(1);
                        updateInfoButton(infoButton, r, c, seatLabel);
                    }
                });

                editButton.addActionListener(e -> {
                    if (seats[r][c] != null && seats[r][c].isReserved()) {
                        JTextField addField = new JTextField();
                        Object[] message = {
                                "추가할 인원 수:", addField
                        };
                        int option = JOptionPane.showConfirmDialog(frame, message, "인원 추가", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            try {
                                int added = Integer.parseInt(addField.getText().trim());
                                if (added <= 0) {
                                    JOptionPane.showMessageDialog(frame, "추가 인원 수는 1명 이상이어야 합니다.");
                                    return;
                                }
                                Seat addedSeat = new Seat(added);
                                additionLogs[r][c].add(addedSeat);
                                updateInfoButton(infoButton, r, c, seatLabel);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(frame, "숫자를 입력하세요.");
                            }
                        }
                    }
                });

                modifyButton.addActionListener(e -> {
                    if (seats[r][c] != null && seats[r][c].isReserved()) {
                        JTextField timeField = new JTextField();
                        Object[] message = {
                                "시작 시간 (hh:mm):", timeField
                        };
                        int option = JOptionPane.showConfirmDialog(frame, message, "시작 시간 수정", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            String timeStr = timeField.getText().trim();
                            try {
                                int hour = Integer.parseInt(timeStr.split(":")[0]);
                                int minute = Integer.parseInt(timeStr.split(":")[1]);
                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime newStart = now.withHour(hour).withMinute(minute).withSecond(0);
                                seats[r][c].setReservedTime(newStart);
                                updateInfoButton(infoButton, r, c, seatLabel);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(frame, "형식이 올바르지 않습니다. 예: 18:30");
                            }
                        }
                    }
                });


                buttonPanel.add(actionButton);
                buttonPanel.add(extendButton);
                buttonPanel.add(editButton);
                buttonPanel.add(modifyButton);
                panel.add(infoButton, BorderLayout.CENTER);
                panel.add(buttonPanel, BorderLayout.SOUTH);
                seatPanel.add(panel);
            }
        }

        frame.add(seatPanel, BorderLayout.CENTER);
        frame.setSize(1000, 1250); 
        frame.setVisible(true);

        new javax.swing.Timer(60000, e -> {
            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 4; col++) {
                    String seatLabel = (char) ('A' + col) + Integer.toString(10 - row);
                    Component comp = seatPanel.getComponent(row * 4 + col);
                    JPanel panel = (JPanel) comp;
                    JButton infoButton = (JButton) panel.getComponent(0);
                    updateInfoButton(infoButton, row, col, seatLabel);
                }
            }
        }).start();
    }

    private void updateInfoButton(JButton btn, int row, int col, String seatLabel) {
        if (seats[row][col] != null && seats[row][col].isReserved()) {
            Seat s = seats[row][col];
            int addedTotal = additionLogs[row][col].stream().mapToInt(Seat::getPeople).sum();
            int total = s.getPeople() + addedTotal;
            String display = s.getPeople() + (addedTotal > 0 ? "(총: " + total + ")" : "");
            
            // 이 좌석의 금액 계산 (기본 인원 + 추가 인원)
            int seatCost = total * COST_PER_PERSON;

            boolean isDeadlineClose = Duration.between(LocalDateTime.now(), s.getDeadlineTimeRaw()).toMinutes() <= 10;
            boolean isAnyAdditionClose = additionLogs[row][col].stream()
                    .anyMatch(seat -> Duration.between(LocalDateTime.now(), seat.getDeadlineTimeRaw()).toMinutes() <= 10);

            btn.setText("<html><center>좌석 " + seatLabel + " | " + display + "명<br>"
                    + s.getReservedTime() + " ~ " + s.getDeadlineTime() + "<br>"
                    + "<b>금액: " + String.format("%,d", seatCost) + "원</b></center></html>");

            if (isDeadlineClose || isAnyAdditionClose) {
                btn.setBackground(Color.MAGENTA);
            } else {
                btn.setBackground(Color.CYAN);
            }
        } else {
            btn.setText("<html><center>좌석 " + seatLabel + "<br>비어 있음</center></html>");
            btn.setBackground(Color.LIGHT_GRAY);
        }
        btn.setFont(new Font("돋움", Font.PLAIN, 11));
    }

    // 총 인원과 총 금액을 계산하고 업데이트하는 메서드
    private void updateTotalInfo() {
        int totalPeople = 0;
        int totalCost = 0;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 4; col++) {
                if (seats[row][col] != null && seats[row][col].isReserved()) {
                    int seatPeople = seats[row][col].getPeople();
                    int addedPeople = additionLogs[row][col].stream().mapToInt(Seat::getPeople).sum();
                    int currentSeatTotal = seatPeople + addedPeople;
                    
                    totalPeople += currentSeatTotal;
                    totalCost += currentSeatTotal * COST_PER_PERSON;
                }
            }
        }

        totalLabel.setText("총 인원: " + totalPeople + "명");
        totalCostLabel.setText("총 금액: " + String.format("%,d", totalCost) + "원");
    }
}