Feature: 스터디를 진행한다

  Scenario: 스터디를 정상 시작한다.
    Given "jinwoo"의 깃허브 아이디로 회원가입을 한다.
    Given "jinwoo"가 제목-"자바1", 정원-"6"명, 최소 주차-"7"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given "noiman"의 깃허브 아이디로 회원가입을 한다.
    Given "yujamint"의 깃허브 아이디로 회원가입을 한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given 깃허브 아이디가 "yujamint"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바1" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바1"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.
    When "jinwoo"가 홈화면을 조회한다.
    Then "yujamint"는 "자바1" 스터디 구성원에 포함되어 있지않다.
    Then 스터디의 남은 날짜가 0이상 6 이하이다.

  Scenario: 스터디가 날짜 변경에 따라 진행된다
    Given "jinwoo"의 깃허브 아이디로 회원가입을 한다.
    Given "jinwoo"가 제목-"자바1", 정원-"6"명, 최소 주차-"7"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given "noiman"의 깃허브 아이디로 회원가입을 한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바1" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바1"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.
    Given 7일이 지난다.
    When "jinwoo"가 "자바1" 스터디를 조회한다.
    Then 스터디의 현재 주차가 2로 변경되어 있다.

  Scenario: 스터디가 최소 주차를 만족하지 못하면 스터디를 종료할 수 없다.
    Given "jinwoo"의 깃허브 아이디로 회원가입을 한다.
    Given "jinwoo"가 제목-"자바1", 정원-"6"명, 최소 주차-"7"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given "noiman"의 깃허브 아이디로 회원가입을 한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바1" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바1"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.

    Given 7일이 지난다.

    Then "jinwoo"가 "자바1" 스터디를 종료할 수 없다.


  Scenario: 스터디가 날짜 변경에 따라 진행되고 종료된다
    Given "jinwoo"의 깃허브 아이디로 회원가입을 한다.
    Given "jinwoo"가 제목-"자바1", 정원-"6"명, 최소 주차-"1"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given "noiman"의 깃허브 아이디로 회원가입을 한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바1" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바1"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.

    Given "jinwoo"가 이름이 "자바1"인 스터디의 현재 주차를 통해 현재 회차를 찾는다.
    Given "jinwoo"가 찾은 회차에 "이번주 머두"로 머스트두를 추가한다.

    When "jinwoo"가 "자바1"스터디 피드에 "내용"의 인증 글을 작성한다.
    When "noiman"가 "자바1"스터디 피드에 "내용"의 인증 글을 작성한다.

    Given 7일이 지난다.

    When "jinwoo"가 "자바1" 스터디를 종료한다.

    When "jinwoo"가 마이페이지를 조회한다.
    Then 조회한 멤버의 경험치가 상승했다.

    When "noiman"가 마이페이지를 조회한다.
    Then 조회한 멤버의 경험치가 상승했다.

  Scenario: 스터디를 탈퇴한다.

    Given "jinwoo"의 깃허브 아이디로 회원가입을 한다.
    Given "jinwoo"가 제목-"자바1", 정원-"6"명, 최소 주차-"1"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given "noiman"의 깃허브 아이디로 회원가입을 한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바1" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바1"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.

    When "noiman" 이 "자바1" 스터디에서 탈퇴한다.

    Then "noiman" 이 "자바1" 스터디에 참여하지 않는다.

  Scenario: 스터디 하나 성공, 하나 실패하면 성공률이 50 퍼센트이다
    Given "jinwoo"의 깃허브 아이디로 회원가입을 한다.
    Given "jinwoo"가 제목-"자바1", 정원-"6"명, 최소 주차-"1"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given "noiman"의 깃허브 아이디로 회원가입을 한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바1"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바1" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바1"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.

    Given "jinwoo"가 이름이 "자바1"인 스터디의 현재 주차를 통해 현재 회차를 찾는다.
    Given "jinwoo"가 찾은 회차에 "이번주 머두"로 머스트두를 추가한다.

    When "jinwoo"가 "자바1"스터디 피드에 "내용"의 인증 글을 작성한다.
    When "noiman"가 "자바1"스터디 피드에 "내용"의 인증 글을 작성한다.

    Given 13일이 지난다.

    When "jinwoo"가 "자바1" 스터디를 종료한다.

    Given "jinwoo"가 제목-"자바2", 정원-"6"명, 최소 주차-"1"주, 주당 진행 횟수-"3"회, 소개-"스터디소개1"로 스터디를 개설한다.
    Given 깃허브 아이디가 "noiman"인 멤버가 이름이 "자바2"스터디에 신청한다.
    Given "jinwoo"가 "noiman"의 "자바2" 스터디 신청을 수락한다.
    Given "jinwoo"가 이름이 "자바2"인 스터디를 "MONDAY"에 진행되도록 하여 시작한다.

    Given "jinwoo"가 이름이 "자바2"인 스터디의 현재 주차를 통해 현재 회차를 찾는다.
    Given "jinwoo"가 찾은 회차에 "이번주 머두"로 머스트두를 추가한다.
    When "noiman" 이 "자바2" 스터디에서 탈퇴한다.
    Given 13일이 지난다.
    When "jinwoo"가 "자바2" 스터디를 종료한다.
    When "noiman"가 마이페이지를 조회한다.

    Then "noiman"의 스터디 성공률이 50 퍼센트이다
