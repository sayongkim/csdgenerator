CSD Generator
=============

CSD Generator는 Eclipse Plugin 입니다.

# 기능

- 텀플릿을 이용해서 Controller, Service, Dao, JSP (목록, 등록, 상세) 소스를 생성합니다.
- 템플릿을 이용해서 Test (Controller, Service, Dao) 소스를 생성합니다.
- Data Source Explorer에서 Database Connection이 등록 되어 있으면 Database Table을 이용해서 MyBatis Mapper, VO를 생성할 수 있습니다.
- Project 설정에서 MyBatis 설정파일이 등록되어 있으면 VO 생성 시 자동으로 Alias를 등록합니다.

# Project 설정

## General Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_01.png">

* Company: 회사명을 설정합니다.
* Author: 작성자를 설정합니다.
* Database Connection: 기본 Database Connection을 설정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

##  Template Group

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_02.png" width="51%" height="51%" ></div>

  - Template Group Name: 템플릿그룹명을 설정합니다.
  - Template Folder: 템플릿이 포함된 폴더를 지정해서 템플릿을 등록할 수 있습니다.
  - Controller Template: Controller 템플릿을 선택합니다.
  - Service Template: Service 템플릿을 선택합니다.
  - Dao Template: Dao 템플릿을 선택합니다.
  - Mapper Tempate: MyBatis Mapper 템플릿을 선택합니다.
  - JSP Template: JSP 템플릿을 선택합니다.

# Test Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_03.png">

* Create test: 테스트소스의 생성여부를 설정합니다.
* Create test controller folder: Test Controller 폴더의 생성여부를 설정합니다.
* Create test service folder: Test Service 폴더 생성여부를 설정합니다.
* Create test dao folder: Test Dao 폴더 생성여부를 설정합니다.
* Test Path: 테스트소스가 생성될 위치를 지정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

##  Test Template Group

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_04.png" width="51%" height="51%" ></div>

  - Template Group Name: 템플릿그룹명을 설정합니다.
  - Template Folder: 템플릿이 포함된 폴더를 지정해서 템플릿을 등록할 수 있습니다.
  - Controller Template: Controller 템플릿을 선택합니다.
  - Service Template: Service 템플릿을 선택합니다.
  - Dao Template: Dao 템플릿을 선택합니다.

# Controller Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_05.png">

기준 Controller 폴더명은 **Controller**입니다.

* Create controller folder: Controller 폴더의 생성여부를 설정합니다.
* Add prefix controller folder name: Prefix를 Controller 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestController
* Create controller sub folder: Controller폴더 하위에 폴더생성여부를 설정합니다.
* Controller Template
  - Add Template... : Controller Template 등록창을 호출합니다.
  - Edit... : Controller Template 수정창을 호출합니다.
  - Remove : 선택된 Controller Template을 삭제합니다.

##  Controller Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_06.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
  
# Service Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_07.png">

기준 Servie 폴더명은 **Service**입니다.

* Create service folder: Service 폴더의 생성여부를 설정합니다.
* Add prefix service folder name: Prefix를 Service 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestService
* Create service sub folder: Service폴더 하위에 폴더생성여부를 설정합니다.
* Create ServiceImpl: Service Impl 소스의 생성여부를 설정합니다.
* Create ServiceImpl folder: Service Impl 폴더의 생성여부를 설정합니다.
* Service Template
  - Add Template... : Service Template 등록창을 호출합니다.
  - Edit... : Service Template 수정창을 호출합니다.
  - Remove : 선택된 Service Template을 삭제합니다.

##  Service Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_08.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
  
# Dao Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_09.png">

기준 Dao 폴더명은 **Dao**입니다.

* Create dao folder: Dao 폴더의 생성여부를 설정합니다.
* Add prefix dao folder name: Prefix를 Dao 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestDao
* Create dao sub folder: Dao폴더 하위에 폴더생성여부를 설정합니다.
* Dao Template
  - Add Template... : Dao Template 등록창을 호출합니다.
  - Edit... : Dao Template 수정창을 호출합니다.
  - Remove : 선택된 Dao Template을 삭제합니다.

##  Dao Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_10.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.

# Mapper Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_11.png">

* Create mapper folder: MyBatis Mapper 소스의 생성여부를 설정합니다.
* Mapper Path: MyBatis Mapper 소스의 생성위치를 지정합니다.
* Mapper Template
  - Add Template... : Mapper Template 등록창을 호출합니다.
  - Edit... : Mapper Template 수정창을 호출합니다.
  - Remove : 선택된 Mapper Template을 삭제합니다.

##  Mapper Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_12.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
