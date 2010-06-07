#!/bin/bash

# Checks parameters
# TODO

#ask for brand version
echo
echo "What cared version you need to install?"
echo " (1) JackSMS"
echo " (2) Aimon"
echo " (3) SmsForFree Lite"
echo " (4) SmsForFree Premium"
echo " (0) Exit"
read -n1 -p "  > " BRANDCODE


#assign parameters based on brand code
case $BRANDCODE in
  1)
      BRAND="jacksms"
  ;;
  2)
      BRAND="aimon"
  ;;
  3)
      BRAND="smsforfree-lite"
  ;;
  4)
      BRAND="smsforfree-premium"
  ;;
  * )
       echo " Abort..."
       echo
       exit 1
  ;;
esac

SOURCEPATH="brand/$BRAND"

# Apply brand
cp -v $SOURCEPATH/res/drawable-hdpi/* ./res/drawable-hdpi/
cp -v $SOURCEPATH/res/drawable-mdpi/* ./res/drawable-mdpi/
cp -v $SOURCEPATH/res/drawable-ldpi/* ./res/drawable-ldpi/
cp -v $SOURCEPATH/res/values/* ./res/values/
 
