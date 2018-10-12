#!/bin/ksh
#=================================================================================================#
# Script Name     : ACC_Create_Extract_files.ksh                                                  #
# Author          : DOYEL SINHA                                                                   # 
# Creation Date   : 03/25/2014                                                                    #
# Description     : EXTRACTING ACCOUNT ALIGNMENT FILES to EAST COAST & MIDWEST SEPERATELY TO BOSS #
# Version         : Initial Version                                                               #
#=================================================================================================#
# CHANGE LOG      :                                                                               #
# VER   Date             Author                Change Details                                     #
# ----  ----------   -------------------  --------------------------------------------------------#
# 1.0   03/25/2014       ZKCI47O            Initial version - ACCOUNT ALIGNMENT                   #
# 2.0   05/19/2014       ZK20ENR            C&RA TO CRH CODE CONVERSION                           #
#=================================================================================================#
set -a
#=================================================================================================#
# INCLUDING THE COMMON LIBRARY FUNCTIONS SCRIPT AND ENVIRONMENT VARIABLES.                        #
#=================================================================================================#
. ${CONFIGDIR}/CRH_LIB
. ${CRH_LKP_DIR}/ACC_Date_calculation.param

#=================================================================================================#
#                         INITIALIZE THE LOAD COUNT VARIABLES
#=================================================================================================#

CRH_SCRIPT_NAME=ACC_Create_Extract_files
CRH_TABLE_NAME=CONS_ACCTALIG_UPD_ECMW_EXT
CRH_LAYER=STAGE
CRH_SYS_ASOF_DT=`date +%F`

#==========================================================================================================================
# DETERMINE THE PROCESS START TIMESTAMP. CALLING THE FUNCTION PROCESS_START_TS. ARGUMENT PASSED IS SCRIPT NAME.
#==========================================================================================================================

FN_PROCESS_START_TS ${CRH_SCRIPT_NAME} ${CRH_DST_DB} ${CRH_TABLE_NAME} ${CRH_LAYER} ${CRH_SYS_ASOF_DT} 
if [ $? -ne 0 ]
then 
    exit ${CRH_FN_PROCESS_START_TS_ERRCODE}
fi

#==========================================================================================================================
# CALLING LOAD AUDIT INSERT TO MAKE AN ENTRY 'P' FOR THE JOB RUN 
#==========================================================================================================================

FN_LOAD_AUDIT_INSERT ${CRH_SUP_DB} ${CRH_SCRIPT_NAME}

#==========================================================================================================================
# Extract files VAPP.CONS.ACCTALIG.EC and VAPP.CONS.ACCTALIG.MW
#==========================================================================================================================
FN_CONS_ACCTALIG_UPD_ECMW_EXT ()
{
${CRH_VSQL_CONN} --echo-all << EOC     

\! echo CRH_TMP_ERRCODE=1 > ${CRH_LKP_DIR}/${CRH_SCRIPT_NAME}.tmp
\! echo "\t VSQL:STARTED EXTRACTING to FILE VAPP.CONS.ACCTALIG.EC"

\! rm -f ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC.dat
\o ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC.dat
          SELECT1
LPAD(CAST(SRC_ACC_CO_NO AS CHAR(4)),4,'0'),
SRC_PROD_CD,
LPAD(CAST(SRC_ACC_NO AS CHAR(21)),21,'0'),
CAI,
LPAD(CAST(CCT_NO AS CHAR(21)),7,'0'),
FILLER
FROM ${CRH_DST_DB}.CONS_ACCTALIG_UPD_ECMW_EXT
WHERE SRC_ACC_CO_NO IN('75','172','358','701','5','406','295','357','35','78','132','134','280','487','493','494');


\! sed -e 's/|//g' ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC.dat > ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC_TEMP.dat

\! rm -f ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC.dat

\! mv ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC_TEMP.dat ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_EC.dat

\o

\! echo CRH_TMP_ERRCODE=2 > ${CRH_LKP_DIR}/${CRH_SCRIPT_NAME}.tmp
\! echo "\t VSQL:STARTED EXTRACTING to FILE VAPP.CONS.ACCTALIG.MW"

\! rm -f  ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW.dat
\o ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW.dat
UPDATE 
LPAD(CAST(SRC_ACC_CO_NO AS CHAR(4)),4,'0'),
SRC_PROD_CD,
LPAD(CAST(SRC_ACC_NO AS CHAR(21)),21,'0'),
CAI,
LPAD(CAST(CCT_NO AS CHAR(21)),7,'0'),
FILLER
FROM ${CRH_DST_DB}.CONS_ACCTALIG_UPD_ECMW_EXT
WHERE SRC_ACC_CO_NO IN('333','849','885','808','778','722','336','794','823','99','342','413','774','343','318','353');

\! sed -e 's/|//g' ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW.dat > ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW_TEMP.dat

\! rm -f ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW.dat

\! mv ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW_TEMP.dat ${CRH_EXTRACT_DIR}/CRH_VAPP_CONS_ACCTALIG_MW.dat

\o 

GRANT ALL ON ${CRH_STG_DB}.PTY_TRAN_SUM_MO TO CRHSSTG;

EOC
if [ $? -ne 0 ]
then
    . ${CRH_LKP_DIR}/${CRH_SCRIPT_NAME}.tmp
	case $CRH_TMP_ERRCODE in
       	 1) CRH_MESSAGE="ERROR OCCURED WHILE EXTRACTING RECORDS TO VAPP.CONS.ACCTALIG.EC";;             
		 2) CRH_MESSAGE="ERROR OCCURED WHILE EXTRACTING RECORDS TO VAPP.CONS.ACCTALIG.MW";;  
	esac
    FN_LOAD_AUDIT_INSERT_FAILURE ${CRH_SUP_DB} ${CRH_SCRIPT_NAME} ${CRH_STG_LAYER} ${CRH_SYS_ASOF_DT} ${CRH_TABLE_NAME} "${CRH_MESSAGE}"
    FN_WRITE_ERROR "${CRH_MESSAGE}"
    exit 8
fi
FN_WRITE_INFO "DATA LOAD COMPLETED SUCCESSFULLY FOR ${CRH_TABLE_NAME}"
}
#========================================================================================================================================#
#                                                                 MAIN                                                                   #
#========================================================================================================================================#
FN_CONS_ACCTALIG_UPD_ECMW_EXT

FN_WRITE_INFO "Done"

FN_WRITE_INFO "Process completed at `date +%Y-%m-%d' '%H:%M:%S`"

# EOD OF SCRIPT
