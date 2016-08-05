CSD Generator
=============

CSD Generator는 Eclipse Plugin 입니다.

# 기능

- 텀플릿을 이용해서 Controller, Service, Dao, JSP (목록, 등록, 상세) 파일을 생성합니다.
- 템플릿을 이용해서 Test (Controller, Service, Dao) 파일을 생성합니다.
- Data Source Explorer에서 Database Connection이 등록 되어 있으면 Database Table을 이용해서 MyBatis Mapper, VO를 생성할 수 있습니다.
- Project 설정에서 MyBatis 설정파일이 등록되어 있으면 VO 생성 시 자동으로 Alias를 등록합니다.

## CSD Generator 실행

**Package Explorer**에서 Class파일이 생성될 위치를 선택 후 오른쪽 마우스로 팝업메뉴에서 **CSD Generator**를 실행합니다.

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_17.png">

* Template: General Tab에 등록된 Template Group이 표시됩니다. 템플릿을 선택합니다.
* Database Connectoin: Data Source Explorer에 등록된 Database Connection이 표시됩니다. Database Connection을 선택하면 Tables:에 Database Table이 출력됩니다.
* Create parent location: Package Explorer에서 선택된 위치의 상단에 생성할 것인지 선택합니다.
* Create folder: Prefix로 폴더를 생성할 것인지 선택합니다.
* Controller: Controller 파일을 생성할 것인지 선택합니다.
* Service: Service 파일을 생성할 것인지 선택합니다.
* Dao: Dao 파일을 생성할 것인지 선택합니다.
* Mapper: Mapper 파일을 생성할 것인지 선택합니다. Project설정에서 Mapper 생성에 체크가 되어 있어야 하며 Database Table을 선택해야 활성화 됩니다.
* VO: VO 파일을 생성할 것인지 선택합니다. Project설정에서 VO 생성에 체크가 되어 있어야 하며 Database Table을 선택해야 활성화 됩니다.
* Superclass: VO 파일을 생성 시 부모클래스를 상속할 것인지 선택합니다.
* JSP: JSP파일을 생성할 것인지 선택합니다.
* Test Tempate: Test Tab에 등록된 Test Template Group이 표시되며 Test 생성에 체크가 되어 있어야 활성화 됩니다. 템플릿을 선택합니다.
* Test Controller: Test Tab에서 Test 생성에 체크가 되어 있어야 활성화 됩니다. Test Controller 파일을 생성할 것인지 선택합니다.
* Test Service: Test Tab에서 Test 생성에 체크가 되어 있어야 활성화 됩니다. Test Service 파일을 생성할 것인지 선택합니다.
* Test Dao: Test Tab에서 Test 생성에 체크가 되어 있어야 활성화 됩니다. Test Dao 파일을 생성할 것인지 선택합니다.
* Vo Superclass: VO 생성 시 상속받을 부모클랙스를 선택합니다.
* Parameter: Hashmap 및 VO Tab에서 VO Path를 지정했을 경우 VO Path하위에 VO가 출력됩니다. Parameter Type을 선택합니다.
* Return: Hashmap 및 VO Tab에서 VO Path를 지정했을 경우 VO Path하위에 VO가 출력됩니다. Return Type을 선택합니다.
* Prefix: 폴더 및 파일명의 기준이 됩니다.
* Regular expressions: Tables을 여러개 선택 시 정규식으로 Prefix를 변경할 수 있습니다.
* Preview: 생성될 폴더 및 파일을 미리 볼 수 있습니다.
* Tables: Database Connection에서 선택된 Database Table이 표시됩니다.

## Project 설정

### General Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_01.png">

* Company: 회사명을 설정합니다.
* Author: 작성자를 설정합니다.
* Database Connection: 기본 Database Connection을 설정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

###  Template Group

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_02.png" width="51%" height="51%" ></div>
:
  - Template Group Name: 템플릿그룹명을 설정합니다.
  - Template Folder: 템플릿이 포함된 폴더를 지정해서 템플릿을 등록할 수 있습니다.
  - Controller Template: Controller 템플릿을 선택합니다.
  - Service Template: Service 템플릿을 선택합니다.
  - Dao Template: Dao 템플릿을 선택합니다.
  - Mapper Tempate: MyBatis Mapper 템플릿을 선택합니다.
  - JSP Template: JSP 템플릿을 선택합니다.

## Test Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_03.png">

* Create test: 테스트파일의 생성여부를 설정합니다.
* Create test controller folder: Test Controller 폴더의 생성여부를 설정합니다.
* Create test service folder: Test Service 폴더 생성여부를 설정합니다.
* Create test dao folder: Test Dao 폴더 생성여부를 설정합니다.
* Test Path: 테스트파일이 생성될 위치를 지정합니다.
* Template Group
  - Add Template... : Tempalte Group 등록창을 호출합니다.
  - Edit... : Template Group 수정창을 호출합니다.
  - Remove : 선택된 Template Group을 삭제합니다.

###  Test Template Group

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_04.png" width="51%" height="51%" ></div>

  - Template Group Name: 템플릿그룹명을 설정합니다.
  - Template Folder: 템플릿이 포함된 폴더를 지정해서 템플릿을 등록할 수 있습니다.
  - Controller Template: Controller 템플릿을 선택합니다.
  - Service Template: Service 템플릿을 선택합니다.
  - Dao Template: Dao 템플릿을 선택합니다.

## Controller Tab

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

###  Controller Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_06.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
  
## Service Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_07.png">

기준 Servie 폴더명은 **Service**입니다.

* Create service folder: Service 폴더의 생성여부를 설정합니다.
* Add prefix service folder name: Prefix를 Service 폴더명에 추가여부를 설정합니다. 
  - 예) Prefix가 Test인 경우 TestService
* Create service sub folder: Service폴더 하위에 폴더생성여부를 설정합니다.
* Create ServiceImpl: Service Impl 파일의 생성여부를 설정합니다.
* Create ServiceImpl folder: Service Impl 폴더의 생성여부를 설정합니다.
* Service Template
  - Add Template... : Service Template 등록창을 호출합니다.
  - Edit... : Service Template 수정창을 호출합니다.
  - Remove : 선택된 Service Template을 삭제합니다.

###  Service Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_08.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.
  
## Dao Tab

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

###  Dao Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_10.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.

## Mapper Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_11.png">

* Create mapper folder: MyBatis Mapper 파일의 생성여부를 설정합니다.
* Mapper Path: MyBatis Mapper 파일이 생성될 폴더를 지정합니다.
* Mapper Template
  - Add Template... : Mapper Template 등록창을 호출합니다.
  - Edit... : Mapper Template 수정창을 호출합니다.
  - Remove : 선택된 Mapper Template을 삭제합니다.

###  Mapper Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_12.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - Template File: 템플릿파일을 선택합니다.

## VO Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_13.png">

* Create Vo: VO의 생성여부를 설정합니다.
* Create Search Vo: 목록등에 사용될 수 있는 검색용 VO의 생성여부를 설정합니다.
* Create vo folder: VO 폴더의 생성여부를 설정합니다.
* Vo Folder Name: VO 폴더명을 설정합니다.
* Vo Path: VO 파일이 생성될 위치를 지정합니다.
* Superclass: 상속받을 Super Class를 선택합니다.
* MyBatis Setting File: MyBatis설정파일을 지정합니다.
* Type Mapping
  - Add Template... : Type Mapping 등록창을 호출합니다.
  - Edit... : Type Mapping 수정창을 호출합니다.
  - Remove : 선택된 항목을 삭제합니다.

###  Type Mapping

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_14.png" width="51%" height="51%" ></div>

  - Data Type: Database Type을 입력합니다.
  - Java Object: Java Object를 선택합니다.

지원되는 Java Object는 다음과 같습니다.

|Java Object|
|:-----------:|
|String|
|BigDecimal|
|Date|
|Timestamp|
|boolean|
|char|
|byte|
|short|
|int|
|long|
|float|
|double|

## JSP Tab

<img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_15.png">

* Create jsp folder: JSP파일의 생성여부를 설정합니다.
* JSPPath: JSP파일이 생성될 폴더를 지정합니다.
* JSPTemplate
  - Add Template... : JSP Template 등록창을 호출합니다.
  - Edit... : JSP Template 수정창을 호출합니다.
  - Remove : 선택된 JSP Template을 삭제합니다.

###  JSP Template

<div align="center"><img src="https://raw.githubusercontent.com/sayongkim/csdgenerator/master/screenshot/screenshot_16.png" width="51%" height="51%" ></div>

  - Template Name: 템플릿명을 설정합니다.
  - List Template File: JSP 목록 템플릿파일을 선택합니다.
  - Post Template File: JSP 등록 템플릿파일을 선택합니다.
  - View Template File: JSP 상세 템플릿파일을 선택합니다.

