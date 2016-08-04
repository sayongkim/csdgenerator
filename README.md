# CSD Generator

CSD Generator는 Eclipse Plugin 입니다.

## 기능

- 텀플릿을 이용해서 Controller, Service, Dao, JSP (목록, 등록, 상세) 소스를 생성합니다.
- 템플릿을 이용해서 Test (Controller, Service, Dao) 소스를 생성합니다.
- Data Source Explorer에서 Database Connection이 등록 되어 있으면 Database Table을 이용해서 MyBatis Mapper, VO를 생성할 수 있습니다.
- Project 설정에서 MyBatis 설정파일이 등록되어 있으면 VO 생성 시 자동으로 Alias를 등록합니다.

## Project 설정

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_01.png" style="max-width:1746px;max-height:1074px;">

* Company: 회사명을 설정합니다.
* Author: 작성자를 설정합니다.
* Database Connection: 기본 Database Connection을 설정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_02.png" width="1004px" height="562px" style="max-width:1004px;max-height:562px;">
