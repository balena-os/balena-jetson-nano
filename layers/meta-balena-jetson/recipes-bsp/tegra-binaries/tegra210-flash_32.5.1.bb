SUMMARY = "Create signed and encrypted binaries for Jetson Nano"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BALENA_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

IMAGE_ROOTFS_ALIGNMENT ?= "4"

DEPENDS = " \
    coreutils-native \
    virtual/bootloader \
    virtual/kernel \
    tegra-binaries \
    tegra-bootfiles \
    tegra210-flashtools-native \
    dtc-native \
"

inherit deploy python3native perlnative

SRC_URI = " \
    file://resinOS-flash210.xml \
    file://partition_specification210.txt \
    file://resinOS-flash210-2gb.xml \
    file://partition_specification210-2gb.txt \
"

VARIANT = ""
VARIANT_jetson-nano-2gb-devkit = "-2gb"
PART_SPEC = "partition_specification210${VARIANT}.txt"
FLASH_XML = "resinOS-flash210${VARIANT}.xml"

# These device trees are not generated by the
# kernel build, this is why we need to specify
# them here instead of machine.conf.
KERNEL_DEVICETREE_jetson-nano = "${DEPLOY_DIR_IMAGE}/tegra210-p3448-0000-p3449-0000-b00.dtb"
KERNEL_DEVICETREE_jetson-nano-emmc = "${DEPLOY_DIR_IMAGE}/tegra210-p3448-0002-p3449-0000-b00.dtb"
KERNEL_DEVICETREE_jn30b-nano = "${DEPLOY_DIR_IMAGE}/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb"
KERNEL_DEVICETREE_photon-nano = "${DEPLOY_DIR_IMAGE}/tegra210-nano-cti-NGX003.dtb"
KERNEL_DEVICETREE_jetson-nano-2gb-devkit = "${DEPLOY_DIR_IMAGE}/tegra210-p3448-0003-p3542-0000.dtb"
KERNEL_DEVICETREE_floyd-nano = "${DEPLOY_DIR_IMAGE}/tegra210-p3448-0002-p3449-0000-b00-floyd-nano.dtb"
DTBFILE = "${@os.path.basename(d.getVar('KERNEL_DEVICETREE', True).split()[0])}"
LNXSIZE ?= "67108864"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
BOOT0="boot0.img"
BL_IS_CBOOT = "${@'1' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '0'}"

LDK_DIR = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"
S = "${WORKDIR}"
LNXFILE="${KERNEL_IMAGETYPE}${KERNEL_INITRAMFS}-${MACHINE}.bin"
IMAGE_TEGRAFLASH_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${LNXFILE}"
BINARY_INSTALL_PATH = "/opt/tegra-binaries"

tegraflash_roundup_size() {
    local actsize=$(stat -L -c "%s" "$1")
    local blks=$(expr \( $actsize + 4095 \) / 4096)
    expr $blks \* 4096
}

BOOTFILES=" \
    eks.img \
    nvtboot_recovery.bin \
    nvtboot.bin \
    nvtboot_cpu.bin \
    warmboot.bin \
    rp4.blob \
    sc7entry-firmware.bin \
"

do_configure() {
    local destdir="${WORKDIR}/tegraflash"
    local lnxfile="${LNXFILE}"
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra210-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    cp "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    cp "${DEPLOY_DIR_IMAGE}/u-boot.bin" ./u-boot.bin

    if [ -n "${KERNEL_ARGS}" ]; then
        fdtput -t s ./${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
    else
        fdtput -d ./${DTBFILE} /chosen bootargs
    fi

    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done

    ln -s "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./cboot.bin
    ln -sf "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./tos-mon-only.img

    ln -s ${STAGING_BINDIR_NATIVE}/tegra210-flash .
    mkdir -p ${DEPLOY_DIR_IMAGE}/bootfiles

    # tegraflash.py script will sign all binaries
    # mentioned for signing in flash.xml.in
    cp ${WORKDIR}/${FLASH_XML} flash.210.in

    sed -i -e "s/\[DTBNAME\]/${DTBFILE}/g" ${WORKDIR}/${PART_SPEC}
    # prep env for tegraflash
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra210-flash/${SOC_FAMILY}-flash-helper.sh ./
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra210-flash/tegraflash.py ./

    rm -rf signed

    local destdir="$1"
    local gptsize="$2"
    local ebtsize=$(tegraflash_roundup_size cboot.bin)
    local nvcsize=$(tegraflash_roundup_size nvtboot.bin)
    local tbcsize=$(tegraflash_roundup_size nvtboot_cpu.bin)
    local dtbsize=$(tegraflash_roundup_size ${DTBFILE})
    local bpfsize=$(tegraflash_roundup_size sc7entry-firmware.bin)
    local wb0size=$(tegraflash_roundup_size warmboot.bin)
    local tossize=$(tegraflash_roundup_size tos-mon-only.img)

    cat "flash.210.in" | sed \
        -e"s,EBTFILE,cboot.bin," -e"s,EBTSIZE,$ebtsize," \
        -e"/NCTFILE/d" -e"s,NCTTYPE,data," \
        -e"/SOSFILE/d" \
        -e"s,NXC,NVC," -e"s,NVCTYPE,bootloader," -e"s,NVCFILE,nvtboot.bin," -e "s,NVCSIZE,$nvcsize," \
        -e"s,MPBTYPE,data," -e"/MPBFILE/d" \
        -e"s,MBPTYPE,data," -e"/MBPFILE/d" \
        -e"s,BXF,BPF," -e"s,BPFFILE,sc7entry-firmware.bin," -e"s,BPFSIZE,$bpfsize," \
        -e"/BPFDTB-FILE/d" \
        -e"s,WX0,WB0," -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,WB0SIZE,$wb0size," \
        -e"s,TXS,TOS," -e"s,TOSFILE,tos-mon-only.img," -e"s,TOSSIZE,$tossize," \
        -e"s,EXS,EKS," -e"s,EKSFILE,eks.img," \
        -e"s,FBTYPE,data," -e"/FBFILE/d" \
        -e"s,DXB,DTB," -e"s,DTBFILE,${DTBFILE}," -e"s,DTBSIZE,$dtbsize," \
        -e"s,TXC,TBC," -e"s,TBCTYPE,bootloader," -e"s,TBCFILE,nvtboot_cpu.bin," -e"s,TBCSIZE,$tbcsize,"  \
        -e"s,EFISIZE,67108864," -e"/EFIFILE/d" \
        -e"s,BCTSIZE,${BOOTPART_SIZE}," -e"s,PPTSIZE,$gptsize," \
        -e"s,PPTFILE,ppt.img," -e"s,GPTFILE,gpt.img," \
        > ./flash.xml

    # The Nano eMMC module does not have a qspi-nor memory to store the boot blob
    if ${@bb.utils.contains('UBOOT_MACHINE', 'p3450-0002_defconfig','true','false',d)}; then
        sed -i 's/device type="spi" instance="0"/device type="sdmmc" instance="3"/g' flash.xml
        sed -i 's/device type="sdcard" instance="0"/device type="sdmmc" instance="3"/g' flash.xml
        dd if=/dev/zero bs=4194304 count=1 > ${BOOT0}
    else
        dd if=/dev/zero bs=4194304 count=1 | tr "\000" "\377" > ${BOOT0}
        dd if=/dev/zero bs=20480 count=1 of=${BOOT0} conv=notrunc
    fi

    # The Nano eMMC module does not have a qspi-nor memory to store the boot blob
    if ${@bb.utils.contains('UBOOT_MACHINE', 'p3450-0002_defconfig','true','false',d)}; then
        sed -i 's/device type="spi" instance="0"/device type="sdmmc" instance="3"/g' flash.xml
        sed -i 's/device type="sdcard" instance="0"/device type="sdmmc" instance="3"/g' flash.xml
        dd if=/dev/zero bs=4194304 count=1 > ${BOOT0}
    else
        dd if=/dev/zero count=4194304 bs=1 | tr "\000" "\377" > ${BOOT0}
        dd if=/dev/zero bs=20480 count=1 of=${BOOT0} conv=notrunc seek=0
    fi
    export _PID=$! ; wait ${_PID} || true

    # Disable cboot displayed vendor logo
    dd if=/dev/zero of=bmp.blob count=1 bs=1

    touch VERFILE
    python3 tegraflash.py --bl cboot.bin --bldtb "${DTBFILE}" --chip 0x21 --applet nvtboot_recovery.bin --bct "${MACHINE}.cfg" --cfg flash.xml --cmd "sign" --keep --odmdata "${ODMDATA}" & \
        export _PID=$! ; wait ${_PID} || true

    rm -rf ${DEPLOY_DIR_IMAGE}/bootfiles/*
    cp -r signed/* ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp -r *.img    ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp -r *.bin    ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp -r *.blob   ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp ${DEPLOY_DIR_IMAGE}/${DTBFILE} ${DEPLOY_DIR_IMAGE}/bootfiles/
    rm -rf ${_PID}

    for off in 0 10240 32768 65536 98304 131072 163840 196608 229376; do
        dd if=${DEPLOY_DIR_IMAGE}/bootfiles/${MACHINE}.bct of=${BOOT0} bs=1 seek=${off} conv=notrunc
    done

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/nvtboot.bin.encrypt of=${BOOT0} bs=1 seek=262144 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/nvtboot.bin.encrypt of=${BOOT0} bs=1 seek=524288 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/nvtboot_cpu.bin.encrypt of=${BOOT0} bs=1 seek=720896 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/${DTBFILE}.encrypt of=${BOOT0} bs=1 seek=851968 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/${DTBFILE}.encrypt of=${BOOT0} bs=1 seek=2555904 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/cboot.bin.encrypt of=${BOOT0} bs=1 seek=1179648 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/warmboot.bin.encrypt of=${BOOT0} bs=1 seek=1769472 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/sc7entry-firmware.bin.encrypt of=${BOOT0} bs=1 seek=1835008 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tos-mon-only.img.encrypt of=${BOOT0} bs=1 seek=2097152 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/u-boot.bin.encrypt of=${BOOT0} bs=1 seek=2883584 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/eks.img of=${BOOT0} bs=1 seek=3637248 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/rp4.blob of=${BOOT0} bs=1 seek=3899392 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/flash.xml.bin of=${BOOT0} bs=1 seek=458752 conv=notrunc

    cp ${S}/tegraflash/${BOOT0} ${DEPLOY_DIR_IMAGE}/bootfiles/
}

do_install() {
     install -d ${D}/${BINARY_INSTALL_PATH}
     cp -r ${S}/tegraflash/signed/* ${D}/${BINARY_INSTALL_PATH}
     cp -rL ${DEPLOY_DIR_IMAGE}/bootfiles/* ${D}/${BINARY_INSTALL_PATH}
     rm ${D}/${BINARY_INSTALL_PATH}/Image-initramfs* ${D}/${BINARY_INSTALL_PATH}/*.dtb ${D}/${BINARY_INSTALL_PATH}/*.xml
     cp ${WORKDIR}/${PART_SPEC} ${D}/${BINARY_INSTALL_PATH}/
}

do_deploy() {
    rm -rf ${DEPLOYDIR}/$(basename ${BINARY_INSTALL_PATH})
    mkdir -p ${DEPLOYDIR}/$(basename ${BINARY_INSTALL_PATH})
    cp -r ${D}/${BINARY_INSTALL_PATH}/* ${DEPLOYDIR}/$(basename ${BINARY_INSTALL_PATH})
}

FILES_${PN} += "${BINARY_INSTALL_PATH}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_install[nostamp] = "1"
do_deploy[nostamp] = "1"
do_configure[nostamp] = "1"

do_configure[depends] += " virtual/bootloader:do_deploy"
do_configure[depends] += " tegra-binaries:do_preconfigure"
do_configure[depends] += " virtual/kernel:do_deploy"
do_configure[depends] += " cboot:do_deploy"
do_configure[depends] += " tos-prebuilt:do_deploy"
do_populate_lic[depends] += " tegra-binaries:do_unpack"

addtask do_deploy before do_package after do_install

COMPATIBLE_MACHINE = "(jetson-nano|jetson-nano-emmc)"
