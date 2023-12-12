class Solution {
    public String solution(String code) {
        String answer = "";
        int mode = 0;
        for (int i = 0; i < code.length(); i++) { //i가 code 길이만큼 돌 때
            if (code.charAt(i) == '1') { //앞에서부터 읽으면서 만약 문자가 1이라면
                mode = 1 - mode; //mode를 1로 바꿈.
            }else if (i % 2 == mode) { // 현재 mode가 0이면 짝수, 1이면 홀수를 뜻함.
                answer += code.charAt(i); //mode가 0인 경우 짝수일 때만 코드에 문자를 더함.
                                          //mode가 1인 경우 홀수일 때만 코드에 문자를 더함.
            }
        }
        return "".equals(answer) ? "EMPTY" : answer; //답이 빈 문자열이라면 empty 출력, 아니라면 그대로 answer 출력
    }
}
