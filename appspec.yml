version: 0.0
os: linux

# 배포 파일 설정
## source: 인스턴스에 복사할 디렉터리 경로
## destination: 인스턴스에서 파일이 복사되는 위치
## overwrite: 복사할 위치에 파일이 있는 경우 대체
files:
  - source:  /
    destination: /home/ubuntu/app
    overwrite: yes

# files 섹션에서 복사한 파일에 대한 권한 설정
## object: 권한이 지정되는 파일 또는 디렉터리
## pattern (optional): 매칭되는 패턴에만 권한 부여
## owner (optional): object 의 소유자
## group (optional): object 의 그룹 이름
permissions:
  - object: /
    pattern: "**"
    owner: ubuntu
    group: ubuntu

# 배포 이후에 실행할 일련의 라이프사이클
# 파일을 설치한 후 `AfterInstall` 에서 기존에 실행중이던 애플리케이션을 종료
# `ApplicationStart` 에서 새로운 애플리케이션을 실행
## location: hooks 에서 실행할 스크립트 위치
## timeout (optional): 스크립트 실행에 허용되는 최대 시간이며, 넘으면 배포 실패로 간주됨
## runas (optional): 스크립트를 실행하는 사용자
hooks:
  AfterInstall:
    - location: scripts/stop.sh
      timeout: 60
      runas: ubuntu
  ApplicationStart:
    - location: scripts/start.sh
      timeout: 60
      runas: ubuntu