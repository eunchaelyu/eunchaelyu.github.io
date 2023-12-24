import java.util.Arrays;

class Solution {
    public int[] solution(int[] arr) {
        if (arr.length <= 1) return new int[]{-1};
        int num = Arrays.stream(arr).min().getAsInt();
        int[] result = Arrays.stream(arr).filter(x -> x != num).toArray();
        return result;
    }
}
