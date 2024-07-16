import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static final String PROGRAMMERS_SIGN_IN = "https://programmers.co.kr/api/v1/account/sign-in";
    private static final String PROGRAMMERS_USER_RECORD = "https://programmers.co.kr/api/v1/users/record";

    public static void main(String[] args) {
        try {
            // 환경 변수를 가져옴
            String id = System.getenv("PROGRAMMERS_TOKEN_ID");
            String pw = System.getenv("PROGRAMMERS_TOKEN_PW");

            if (id == null || pw == null) {
                System.out.println("환경 변수 PROGRAMMERS_TOKEN_ID 와 PROGRAMMERS_TOKEN_PW를 설정해주세요.");
                return;
            }

            HttpClient client = HttpClient.newHttpClient();

            // 프로그래머스 로그인
            String signInPayload = String.format("{\"user\": {\"email\": \"%s\", \"password\": \"%s\"}}", id, pw);
            HttpRequest signInRequest = HttpRequest.newBuilder()
                    .uri(new URI(PROGRAMMERS_SIGN_IN))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(signInPayload))
                    .build();

            HttpResponse<String> signInResponse = client.send(signInRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(signInResponse);
            if (signInResponse.statusCode() != 200) {
                System.err.println("로그인 실패: " + signInResponse.body());
                return;
            }

            // 모든 쿠키를 추출
            List<String> cookies = signInResponse.headers().allValues("set-cookie").stream()
                    .map(cookie -> cookie.split(";", 2)[0])  // 쿠키 값만 추출
                    .collect(Collectors.toList());
            String cookiesHeader = String.join("; ", cookies);

            // 사용자 정보 요청
            HttpRequest userRecordRequest = HttpRequest.newBuilder()
                    .uri(new URI(PROGRAMMERS_USER_RECORD))
                    .header("Cookie", cookiesHeader)
                    .GET()
                    .build();

            HttpResponse<String> userRecordResponse = client.send(userRecordRequest, HttpResponse.BodyHandlers.ofString());

            if (userRecordResponse.statusCode() != 200) {
                System.err.println("----");
                System.err.println(userRecordResponse);
                System.err.println("사용자 정보 요청 실패: " + userRecordResponse.body());
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> myData = objectMapper.readValue(userRecordResponse.body(), Map.class);

            // svg 뱃지 생성
            if (myData != null) {
                // 데이터를 안전하게 가져오기 위해 변수에 저장
                Object level = ((Map<String, Object>) myData.get("skillCheck")).get("level");
                Object score = ((Map<String, Object>) myData.get("ranking")).get("score");
                Object solved = ((Map<String, Object>) myData.get("codingTest")).get("solved");
                Object rank = ((Map<String, Object>) myData.get("ranking")).get("rank");

                // 데이터를 String 타입으로 변환하여 안전하게 사용
                String svgContent = String.format(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"551px\" height=\"217px\" style=\"shape-rendering:geometricPrecision; text-rendering:geometricPrecision; image-rendering:optimizeQuality; fill-rule:evenodd; clip-rule:evenodd\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
                                "<style>" +
                                ".title {" +
                                "fill: #434343;" +
                                "font-size: 1rem;" +
                                "line-height: 1.5rem;" +
                                "font-weight: bold;" +
                                "font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji;" +
                                "}" +
                                ".desc {" +
                                "fill: #434343;" +
                                "font-size: 2.5rem;" +
                                "font-weight: bold;" +
                                "line-height: 1.5rem;" +
                                "font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji;" +
                                "}" +
                                ".desc-2 {" +
                                "fill: #434343;" +
                                "font-size: 1rem;" +
                                "font-weight: bold;" +
                                "line-height: 1.5rem;" +
                                "font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji;" +
                                "}" +
                                ".text, .desc {" +
                                "animation: twinkling 4s ease-in-out infinite;" +
                                "}" +
                                "@keyframes twinkling {" +
                                "40%% { opacity: 1; }" +
                                "50%% { opacity: 0.5; }" +
                                "60%% { opacity: 1; }" +
                                "70%% { opacity: 0.5; }" +
                                "80%% { opacity: 1; }" +
                                "}" +
                                "</style>" +

                                "<g><path style=\"opacity:1\" fill=\"#fefefe\" d=\"M -0.5,-0.5 C 183.167,-0.5 366.833,-0.5 550.5,-0.5C 550.5,71.8333 550.5,144.167 550.5,216.5C 366.833,216.5 183.167,216.5 -0.5,216.5C -0.5,212.167 -0.5,207.833 -0.5,203.5C 1.14011,204.32 2.80678,205.153 4.5,206C 88.1667,206.667 171.833,206.667 255.5,206C 259,205.167 261.167,203 262,199.5C 262.667,175.167 262.667,150.833 262,126.5C 261.167,123 259,120.833 255.5,120C 171.833,119.333 88.1667,119.333 4.5,120C 2.80678,120.847 1.14011,121.68 -0.5,122.5C -0.5,115.167 -0.5,107.833 -0.5,100.5C 1.14011,101.32 2.80678,102.153 4.5,103C 88.1667,103.667 171.833,103.667 255.5,103C 259,102.167 261.167,100 262,96.5C 262.667,72.1667 262.667,47.8333 262,23.5C 261.167,20 259,17.8333 255.5,17C 171.833,16.3333 88.1667,16.3333 4.5,17C 2.80678,17.8466 1.14011,18.6799 -0.5,19.5C -0.5,12.8333 -0.5,6.16667 -0.5,-0.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#ecf5ff\" d=\"M -0.5,100.5 C -0.5,73.5 -0.5,46.5 -0.5,19.5C 1.14011,18.6799 2.80678,17.8466 4.5,17C 88.1667,16.3333 171.833,16.3333 255.5,17C 259,17.8333 261.167,20 262,23.5C 262.667,47.8333 262.667,72.1667 262,96.5C 261.167,100 259,102.167 255.5,103C 171.833,103.667 88.1667,103.667 4.5,103C 2.80678,102.153 1.14011,101.32 -0.5,100.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#ecf5ff\" d=\"M 285.5,16.5 C 369.167,16.3333 452.834,16.5 536.5,17C 540,17.8333 542.167,20 543,23.5C 543.667,47.8333 543.667,72.1667 543,96.5C 542.167,100 540,102.167 536.5,103C 452.833,103.667 369.167,103.667 285.5,103C 282,102.167 279.833,100 279,96.5C 278.333,72.1667 278.333,47.8333 279,23.5C 280.018,19.9853 282.185,17.652 285.5,16.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#cdeafd\" d=\"M 520.5,56.5 C 521.583,56.5394 522.583,56.8728 523.5,57.5C 524.498,65.4723 524.831,73.4723 524.5,81.5C 523.883,81.3893 523.383,81.056 523,80.5C 522.667,76.1667 522.333,71.8333 522,67.5C 521.617,66.944 521.117,66.6107 520.5,66.5C 520.5,63.1667 520.5,59.8333 520.5,56.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#abcfff\" d=\"M 527.5,55.5 C 528.566,54.3079 529.399,54.6412 530,56.5C 530.667,64.1667 530.667,71.8333 530,79.5C 529.311,80.3567 528.478,81.0233 527.5,81.5C 528.758,73.0803 528.758,64.4136 527.5,55.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#bbdcfd\" d=\"M 218.5,80.5 C 219.657,77.7273 220.324,74.7273 220.5,71.5C 222.49,63.5101 227.49,58.5101 235.5,56.5C 238.528,56.8473 241.194,58.014 243.5,60C 241.744,60.1405 240.078,59.6405 238.5,58.5C 229.247,62.6066 223.914,69.7733 222.5,80C 222.565,82.5259 223.232,84.8593 224.5,87C 221.07,86.3974 219.07,84.2307 218.5,80.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#cde9fd\" d=\"M 241.5,77.5 C 240.833,77.5 240.5,77.8333 240.5,78.5C 238.628,81.5287 235.961,83.6954 232.5,85C 230.536,85.624 228.869,85.124 227.5,83.5C 225.021,73.7912 228.354,67.1245 237.5,63.5C 239.374,63.3898 240.874,64.0564 242,65.5C 242.497,69.5239 242.331,73.5239 241.5,77.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#fcfdfe\" d=\"M 234.5,67.5 C 237.826,67.1622 239.493,68.6622 239.5,72C 238.334,76.434 237,76.2674 235.5,71.5C 232.832,72.515 232.166,74.1817 233.5,76.5C 235.017,75.0892 236.351,75.2559 237.5,77C 234.006,81.7713 231.34,81.6046 229.5,76.5C 229.904,72.7712 231.571,69.7712 234.5,67.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#a1ccfe\" d=\"M 520.5,56.5 C 520.5,59.8333 520.5,63.1667 520.5,66.5C 517.238,64.9225 514.572,65.5892 512.5,68.5C 512.617,72.8097 512.617,76.8097 512.5,80.5C 510.86,79.6799 509.193,78.8466 507.5,78C 508.448,77.5172 509.448,77.3505 510.5,77.5C 510.335,74.1501 510.502,70.8168 511,67.5C 512.995,65.253 515.495,64.253 518.5,64.5C 518.336,61.8127 518.503,59.146 519,56.5C 520.731,53.9686 521.231,53.9686 520.5,56.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#cde9fd\" d=\"M 512.5,68.5 C 513.822,68.33 514.989,68.6634 516,69.5C 516.499,74.8229 516.666,80.1563 516.5,85.5C 514.955,84.7703 514.122,83.437 514,81.5C 513.22,83.3869 513.387,85.0536 514.5,86.5C 514.672,87.4916 514.338,88.1583 513.5,88.5C 513.167,88.5 512.833,88.5 512.5,88.5C 512.5,85.8333 512.5,83.1667 512.5,80.5C 512.617,76.8097 512.617,72.8097 512.5,68.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#eef6fe\" d=\"M 520.5,66.5 C 521.407,72.1064 521.74,77.773 521.5,83.5C 519.95,84.4408 518.284,85.1075 516.5,85.5C 516.666,80.1563 516.499,74.8229 516,69.5C 514.989,68.6634 513.822,68.33 512.5,68.5C 514.572,65.5892 517.238,64.9225 520.5,66.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#88bcff\" d=\"M 520.5,66.5 C 521.117,66.6107 521.617,66.944 522,67.5C 522.333,71.8333 522.667,76.1667 523,80.5C 523.383,81.056 523.883,81.3893 524.5,81.5C 524.445,82.4692 523.445,83.1358 521.5,83.5C 521.74,77.773 521.407,72.1064 520.5,66.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#82b5fe\" d=\"M 220.5,71.5 C 220.324,74.7273 219.657,77.7273 218.5,80.5C 218.215,77.1709 218.882,74.1709 220.5,71.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#a3cbfe\" d=\"M 240.5,78.5 C 240.5,77.8333 240.833,77.5 241.5,77.5C 243.391,78.5361 245.224,78.5361 247,77.5C 249.391,78.4291 251.225,79.9291 252.5,82C 251.681,83.3188 250.681,84.4855 249.5,85.5C 249.409,83.7769 249.075,81.7769 248.5,79.5C 247.167,79.5 245.833,79.5 244.5,79.5C 244.719,80.675 244.386,81.675 243.5,82.5C 242.575,81.3595 241.741,81.3595 241,82.5C 241.331,80.9813 241.164,79.648 240.5,78.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#b7dbfd\" d=\"M 512.5,88.5 C 512.833,88.5 513.167,88.5 513.5,88.5C 511.199,91.857 508.199,92.357 504.5,90C 501.872,86.5349 501.872,83.0349 504.5,79.5C 505.696,79.8457 505.696,80.3457 504.5,81C 505.5,81.5 506.5,82 507.5,82.5C 508.167,85.1667 508.833,87.8333 509.5,90.5C 510.739,90.0576 511.739,89.3909 512.5,88.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#92c1fe\" d=\"M 516.5,85.5 C 518.284,85.1075 519.95,84.4408 521.5,83.5C 521.423,84.7498 520.756,85.5831 519.5,86C 517.866,86.4935 516.199,86.6602 514.5,86.5C 513.387,85.0536 513.22,83.3869 514,81.5C 514.122,83.437 514.955,84.7703 516.5,85.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#ecf5fe\" d=\"M -0.5,203.5 C -0.5,176.5 -0.5,149.5 -0.5,122.5C 1.14011,121.68 2.80678,120.847 4.5,120C 88.1667,119.333 171.833,119.333 255.5,120C 259,120.833 261.167,123 262,126.5C 262.667,150.833 262.667,175.167 262,199.5C 261.167,203 259,205.167 255.5,206C 171.833,206.667 88.1667,206.667 4.5,206C 2.80678,205.153 1.14011,204.32 -0.5,203.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#ecf5fe\" d=\"M 285.5,119.5 C 369.167,119.333 452.834,119.5 536.5,120C 540,120.833 542.167,123 543,126.5C 543.667,150.833 543.667,175.167 543,199.5C 542.167,203 540,205.167 536.5,206C 452.833,206.667 369.167,206.667 285.5,206C 282,205.167 279.833,203 279,199.5C 278.333,175.167 278.333,150.833 279,126.5C 280.018,122.985 282.185,120.652 285.5,119.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#fdfdfe\" d=\"M 246.5,165.5 C 246.758,169.367 246.092,173.034 244.5,176.5C 243.662,176.842 243.328,177.508 243.5,178.5C 242.272,179.306 241.272,180.306 240.5,181.5C 240.5,180.833 240.167,180.5 239.5,180.5C 239.5,178.833 239.5,177.167 239.5,175.5C 240.369,173.283 240.702,170.95 240.5,168.5C 239.209,168.263 238.209,168.596 237.5,169.5C 236.504,169.414 235.671,169.748 235,170.5C 234.503,173.146 234.336,175.813 234.5,178.5C 234.737,179.791 234.404,180.791 233.5,181.5C 230.986,177.806 228.986,173.806 227.5,169.5C 229.309,160.957 234.309,158.124 242.5,161C 244.07,162.397 245.404,163.897 246.5,165.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#f5faff\" d=\"M 521.5,189.5 C 520.833,189.5 520.5,189.833 520.5,190.5C 518.181,190.992 516.181,191.992 514.5,193.5C 512.565,194.15 510.565,194.483 508.5,194.5C 508.666,185.827 508.5,177.16 508,168.5C 508.141,177.227 507.308,185.56 505.5,193.5C 505.334,184.827 505.5,176.16 506,167.5C 508.527,165.903 511.194,164.57 514,163.5C 515.011,163.892 515.511,164.559 515.5,165.5C 520.21,162.641 525.21,160.641 530.5,159.5C 530.831,168.192 530.498,176.859 529.5,185.5C 526.807,186.847 524.14,188.18 521.5,189.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#b6d7fd\" d=\"M 521.5,189.5 C 524.14,188.18 526.807,186.847 529.5,185.5C 530.498,176.859 530.831,168.192 530.5,159.5C 525.21,160.641 520.21,162.641 515.5,165.5C 515.511,164.559 515.011,163.892 514,163.5C 511.194,164.57 508.527,165.903 506,167.5C 505.5,176.16 505.334,184.827 505.5,193.5C 503.089,193.863 501.589,192.863 501,190.5C 500.333,182.5 500.333,174.5 501,166.5C 508.173,162.246 515.507,158.246 523,154.5C 525.304,156.068 527.804,157.235 530.5,158C 531.126,158.75 531.626,159.584 532,160.5C 532.667,168.5 532.667,176.5 532,184.5C 529.292,187.111 526.125,189.111 522.5,190.5C 521.893,190.376 521.56,190.043 521.5,189.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#c3e5fe\" d=\"M 517.5,168.5 C 520.317,168.801 520.984,170.134 519.5,172.5C 521.439,174.122 521.772,175.956 520.5,178C 518.167,179.167 515.833,180.333 513.5,181.5C 513.167,181.167 512.833,180.833 512.5,180.5C 514.414,176.515 516.081,172.515 517.5,168.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#7ab1fc\" d=\"M 237.5,169.5 C 238.043,169.56 238.376,169.893 238.5,170.5C 236.395,172.836 235.062,175.503 234.5,178.5C 234.336,175.813 234.503,173.146 235,170.5C 235.671,169.748 236.504,169.414 237.5,169.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#afd3ff\" d=\"M 514.5,193.5 C 513.378,194.947 511.878,195.947 510,196.5C 508.348,195.601 506.848,194.601 505.5,193.5C 507.308,185.56 508.141,177.227 508,168.5C 508.5,177.16 508.666,185.827 508.5,194.5C 510.565,194.483 512.565,194.15 514.5,193.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#9cc3fd\" d=\"M 246.5,165.5 C 247.634,167.966 247.801,170.633 247,173.5C 246.623,174.942 245.79,175.942 244.5,176.5C 246.092,173.034 246.758,169.367 246.5,165.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#62a6fc\" d=\"M 239.5,175.5 C 239.5,177.167 239.5,178.833 239.5,180.5C 239.483,182.028 238.816,182.528 237.5,182C 238.117,179.723 238.784,177.556 239.5,175.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#daedfe\" d=\"M 237.5,169.5 C 238.209,168.596 239.209,168.263 240.5,168.5C 240.702,170.95 240.369,173.283 239.5,175.5C 238.784,177.556 238.117,179.723 237.5,182C 238.816,182.528 239.483,182.028 239.5,180.5C 240.167,180.5 240.5,180.833 240.5,181.5C 239.918,183.669 239.918,186.002 240.5,188.5C 238.473,188.662 236.473,188.495 234.5,188C 234.043,187.586 233.709,187.086 233.5,186.5C 235.078,185.702 236.744,185.202 238.5,185C 237.5,184.667 236.5,184.333 235.5,184C 236.554,182.612 236.72,181.112 236,179.5C 235.751,180.624 235.251,181.624 234.5,182.5C 233.893,182.376 233.56,182.043 233.5,181.5C 234.404,180.791 234.737,179.791 234.5,178.5C 235.062,175.503 236.395,172.836 238.5,170.5C 238.376,169.893 238.043,169.56 237.5,169.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#8bbcfd\" d=\"M 243.5,178.5 C 242.395,182.803 241.062,186.969 239.5,191C 232.857,190.847 230.857,187.68 233.5,181.5C 233.56,182.043 233.893,182.376 234.5,182.5C 235.251,181.624 235.751,180.624 236,179.5C 236.72,181.112 236.554,182.612 235.5,184C 236.5,184.333 237.5,184.667 238.5,185C 236.744,185.202 235.078,185.702 233.5,186.5C 233.709,187.086 234.043,187.586 234.5,188C 236.473,188.495 238.473,188.662 240.5,188.5C 239.918,186.002 239.918,183.669 240.5,181.5C 241.272,180.306 242.272,179.306 243.5,178.5 Z\"/></g>\n" +
                                "<g><path style=\"opacity:1\" fill=\"#a4c8ff\" d=\"M 520.5,190.5 C 519.089,192.539 517.089,193.539 514.5,193.5C 516.181,191.992 518.181,190.992 520.5,190.5 Z\"/></g>\n" +

                                "<text text-anchor=\"middle\" x=\"80\" y=\"45\" class=\"title\" style=\"fill:#0078ff;\" stroke=\"#none\" stroke-width=\"1\" >정복 중인 레벨</text>" +
                                "<text text-anchor=\"middle\" x=\"60\" y=\"85\" class=\"desc\" style=\"fill:#000000;\" stroke=\"#none\" stroke-width=\"1\" >%s</text>" +
                                "<text text-anchor=\"middle\" x=\"100\" y=\"85\" class=\"desc-2\" style=\"fill:#434343;\" stroke=\"#none\" stroke-width=\"1\" >레벨</text>" +

                                "<text text-anchor=\"middle\" x=\"340\" y=\"45\" class=\"title\" style=\"fill:#0078ff;\" stroke=\"#none\" stroke-width=\"1\" >현재 점수</text>" +
                                "<text text-anchor=\"middle\" x=\"360\" y=\"85\" class=\"desc\" style=\"fill:#000000;\" stroke=\"#none\" stroke-width=\"1\" >%s</text>" +
                                "<text text-anchor=\"middle\" x=\"450\" y=\"85\" class=\"desc-2\" style=\"fill:#434343;\" stroke=\"#none\" stroke-width=\"1\" >점</text>" +

                                "<text text-anchor=\"middle\" x=\"100\" y=\"150\" class=\"title\" style=\"fill:#0078ff;\" stroke=\"#none\" stroke-width=\"1\" >해결한 코딩 테스트</text>" +
                                "<text text-anchor=\"middle\" x=\"65\" y=\"190\" class=\"desc\" style=\"fill:#000000;\" stroke=\"#none\" stroke-width=\"1\" >%s</text>" +
                                "<text text-anchor=\"middle\" x=\"120\" y=\"190\" class=\"desc-2\" style=\"fill:#434343;\" stroke=\"#none\" stroke-width=\"1\" >문제</text>" +

                                "<text text-anchor=\"middle\" x=\"340\" y=\"150\" class=\"title\" style=\"fill:#0078ff;\" stroke=\"#none\" stroke-width=\"1\" >나의 랭킹</text>\n" +
                                "            <text text-anchor=\"middle\" x=\"370\" y=\"190\" class=\"desc\" style=\"fill:#000000;\" stroke=\"#none\" stroke-width=\"1\" >%s</text>\n" +
                                "            <text text-anchor=\"middle\" x=\"450\" y=\"190\" class=\"desc-2\" style=\"fill:#434343;\" stroke=\"#none\" stroke-width=\"1\" >위</text>" +
                                "</svg>",
                        level != null ? level.toString() : "",
                        score != null ? score.toString() : "",
                        solved != null ? solved.toString() : "",
                        rank != null ? rank.toString() : ""
                );

                Path currentPath = Paths.get("").toAbsolutePath();
                System.out.println("현재 작업 디렉토리: " + currentPath);

                Path fileDirectory = Paths.get("./result");
                if (!Files.exists(fileDirectory)) {
                    Files.createDirectories(fileDirectory);  // dist 디렉토리가 없으면 생성
                }

                Path templateFile = fileDirectory.resolve("result.svg");
                if (!Files.exists(templateFile)) {
                    Files.writeString(templateFile, "");  // result 파일이 없으면 빈 파일 생성
                }

                Path resultFile = fileDirectory.resolve("result.svg");

                // svg 뱃지 파일 생성
                Files.writeString(resultFile, svgContent);
                System.out.println("뱃지 생성 성공");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
