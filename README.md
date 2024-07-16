# 개요

깃허브 프로필을 꾸미다가 백준 티어는 뱃지가 있는데 프로그래머스는 없어서 아쉬웠다. 그러던 중 https://github.com/libtv/github-programmers-rank 의 레포를 발견하고 코드 분석 후 자바로 언어를 변경하여 뱃지 생성 프로젝트를 만들었다.

하지만 프로그래머스에서 문제를 풀고 바로 뱃지가 반영되기를 원해서 백준 허브와 연동해서 프로그래머스에서 문제를 풀면 바로 자동으로 뱃지가 생성 및 업데이트 되고 이를 깃허브 프로필에서 볼 수 있도록 하였다.


# 전제 조건

[백준 허브](https://bit.ly/3XR66UE)가 깔려서 프로그래머스에서 문제를 풀면 깃허브에 자동으로 커밋되는 레포가 있어야 한다.

# 작동 원리

1. 프로그래머스에서 문제를 푼다.
2. 백준 허브를 통해 지정한 레포에 커밋이 된다.
3. `GitHub Action` 을 통해 프로그래머스 뱃지 생성 프로젝트가 `dispatch` 되면서 뱃지가 생성 및 업데이트 된다.
![](https://velog.velcdn.com/images/tomy8964/post/569d2ea8-6d3b-4154-a733-5ef85cc844de/image.png)
4. 깃허브 프로필에 프로그래머스 뱃지가 반영된다. `![Programmers Badge](https://raw.githubusercontent.com/{깃허브 아이디}/Programmers_Badge_Generator/main/result/result.svg?cache_buster=1)`

# How to install

## 1. [Fork Project - 프로젝트를 포크합니다.](https://github.com/tomy8964/Programmers_Badge_Generator) (링크 클릭시 이동)

![](https://velog.velcdn.com/images/tomy8964/post/76302289-438a-44a3-8092-e4777515b97a/image.png)

![](https://velog.velcdn.com/images/tomy8964/post/7513b3c4-544a-4cd4-a92c-bf6c12a52c92/image.png)

## 2. Apply Secrets - 시크릿에 키를 등록합니다.

`Settings - Secrets and variables - Actions`

![](https://velog.velcdn.com/images/tomy8964/post/76711823-cf5c-4316-88e0-5a31e06ff654/image.png)

- 커밋시 사용하기 위한 값
  - `GH_PAT` : `GitHub Personal Access Token` 값 입니다. (처음 한번만 볼 수 있기 때문에 복사해서 메모장에 붙여둡니다.)
    - 발급 방법
      1. 오른쪽 위의 내 프로필 사진 클릭
      
      2. `Settings` 클릭
      3. 왼쪽 제일 아래에 있는 `Developer settings` 클릭
      4. `Personal access tokens` - `Tokens (classic)` 클릭
      5. 오른쪽 위의 `Generate new token(classic)` 클릭
      6. 기억할 토큰 이름 `Note` 에 입력
      7. `Expiration` 만료 기간 지정
      8. `Select scopes` - `repo` `workflow` 선택 ![](https://velog.velcdn.com/images/tomy8964/post/b8b870b4-1bfa-4663-8ddc-92f5f7bc44fa/image.png)
      9. `generate token` 클릭 후 토큰 값 복사해서 메모장에 붙여놓기
  - `GIT_EMAIL` : 깃허브 이메일
  
  - `GIT_NAME` : 깃허브 이름
- 프로그래머스 정보를 가져오기 위한 값
  - `PROGRAMMERS_TOKEN_ID` : 프로그래머스 아이디 값
  
  - `PROGRAMMERS_TOKEN_PW` : 프로그래머스 패스워드 값

## 3. set Github Action - 깃허브 액션을 설정합니다.

1. 화면 위쪽 가운데 있는 `Actions` 클릭

2. `understand` 클릭

3. 왼쪽에 `programmers_badge_action` 클릭 후 `Enable workflow` 클릭![](https://velog.velcdn.com/images/tomy8964/post/8c2381eb-5cd7-4562-9123-00c05f243f96/image.png)

## 4. 백준 허브로 코딩테스트 문제 자동 커밋되는 레포로 이동
1. `Settings` 클릭
2. `Secrets and variables` - `Actions` 에 `GH_PAT` 값 추가 ![](https://velog.velcdn.com/images/tomy8964/post/d6bb33f7-e81b-4760-a2ad-ea76d8639749/image.png)

3. `Actions` 클릭
4. `set up a workflow yourself` 클릭 ![](https://velog.velcdn.com/images/tomy8964/post/9e008204-db81-4367-8fad-d72ca84e0b4a/image.png)
5. 밑의 `dispatch-workflow.yml` 복붙 후 `yml` 내용 중 깃허브 이름 부분 변경

```yml
name: dispatch-workflow

on:
 push:
   branches:
     - main

jobs:
 dispatch:
   runs-on: ubuntu-latest
   steps:
     - name: Trigger repository dispatch
       uses: peter-evans/repository-dispatch@v1
       with:
         token: ${{ secrets.GH_PAT }}
         repository: {자기 깃허브 이름}/Programmers_Badge_Generator
         event-type: trigger-workflow
```
![](https://velog.velcdn.com/images/tomy8964/post/db5a144a-4cb0-4e50-894e-1f3d2a652d68/image.png)


6. 오른쪽 위의 `Commit changes..` 클릭 후 커밋

7. `Actions` 클릭 후 잘 돌아가는지 확인![](https://velog.velcdn.com/images/tomy8964/post/d2119fa9-155b-4050-a690-c56d798da0a0/image.png)


## 5. 프로그래머스 문제 풀기

1. 프로그래머스 접속 후 아무 문제 풀기
![](https://velog.velcdn.com/images/tomy8964/post/9947050f-50b8-4ed1-8bfe-4bd6f5ba71df/image.png)

2. 백준 허브 커밋 후 자동으로 `dispatch` 되는지 확인![](https://velog.velcdn.com/images/tomy8964/post/56091dfa-6e1f-4d2b-b137-1ad269b13390/image.png)

## 6. 뱃지 생성 확인

1. 포크했던 `Programmers_Badge_Generator` 레포로 이동

2. `result` 폴더에 `result.svg` 생성된 결과 자신의 정보와 맞는지 확인![](https://velog.velcdn.com/images/tomy8964/post/c05affa9-38dc-476b-a9f0-500329c1ec42/image.png)

## 7. 깃허브 프로필에 프로그래머스 뱃지 등록

1. 자신의 이름과 같은 레포의 `README.md` 수정

2. `![Programmers Badge](https://raw.githubusercontent.com/{자기신 깃허브 아이디}/Programmers_Badge_Generator/main/result/result.svg)` 삽입![](https://velog.velcdn.com/images/tomy8964/post/85249ad1-3262-4767-a063-c346541f536f/image.png)


3. 뱃지 생성 확인![](https://velog.velcdn.com/images/tomy8964/post/ce269ec3-c304-4b3b-a0a6-3473696f772b/image.png)

## 8. 프로그래머스에서 문제를 풀면 자동으로 뱃지가 업데이트된다.

![](https://velog.velcdn.com/images/tomy8964/post/fa8587c5-51ef-46aa-b6c6-f9473925adbb/image.png)

> 이 프로젝트를 개선하고 싶으시거나 문의 사항 있으면 댓글과 `pull request` 해주시면 감사하겠습니다.
