SUMMARY = "Create flash artifacts without flashing the Jetson NX Devkit eMMC"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BALENA_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

IMAGE_ROOTFS_ALIGNMENT ?= "4"

DEPENDS = " \
    coreutils-native \
    virtual/bootloader \
    virtual/kernel \
    tegra-binaries \
    tegra-bootfiles \
    tegra194-flashtools-native \
    dtc-native \
    virtual/bootlogo \
    "

inherit deploy python3native perlnative

BOOT_BINDIFF="boot0_t194_nx_emmc.bindiff"

SRC_URI = " \
    file://resinOS-flash194_nxde.xml \
    file://partition_specification194_nxde.txt \
    file://${BOOT_BINDIFF} \
"

FLASHXML = "resinOS-flash194_nxde.xml"
DTBNAME = "tegra194-p3668-all-p3509-0000"
DTBNAME_photon-xavier-nx = "tegra194-xavier-nx-cti-NGX003"
KERNEL_DEVICETREE = "${DEPLOY_DIR_IMAGE}/${DTBNAME}.dtb"
DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE', True).split()[0])}"
LNXSIZE ?= "67108864"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"

LDK_DIR = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"
S = "${WORKDIR}"
LNXFILE="${KERNEL_IMAGETYPE}${KERNEL_INITRAMFS}-${MACHINE}.bin"
IMAGE_TEGRAFLASH_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${LNXFILE}"
BINARY_INSTALL_PATH = "/opt/tegra-binaries"

OS_KERNEL_CMDLINE = "${@bb.utils.contains('DEVELOPMENT_IMAGE','1','console=ttyTHS0,115200n8 console=tty1 debug loglevel=7','console=null quiet splash vt.global_cursor_default=0 consoleblank=0',d)}"
ROOTA_ARGS="root=LABEL=resin-rootA ro rootwait rootfstype=ext4 ${KERNEL_ARGS} ${OS_KERNEL_CMDLINE}"
ROOTB_ARGS="root=LABEL=resin-rootB ro rootwait rootfstype=ext4 ${KERNEL_ARGS} ${OS_KERNEL_CMDLINE}"

BOOTFILES = "\
    adsp-fw.bin \
    bpmp_t194.bin \
    camera-rtcpu-rce.img \
    eks.img \
    mb1_t194_prod.bin \
    nvtboot_applet_t194.bin \
    nvtboot_t194.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpu_t194.bin \
    nvtboot_recovery_t194.bin \
    nvtboot_recovery_cpu_t194.bin \
    preboot_d15_prod_cr.bin \
    slot_metadata.bin \
    spe_t194.bin \
    warmboot_t194_prod.bin \
    xusb_sil_rel_fw \
    cbo.dtb \
"

signfile() {
    local destdir="${WORKDIR}/tegraflash"
    local lnxfile="${LNXFILE}"
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    export cbootfilename=cboot_t194.bin

    export BOARDID=${TEGRA_BOARDID}
    export FAB=${TEGRA_FAB}
    export localbootfile=boot.img

    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    . ./flashvars

    export bins=" mb2_bootloader nvtboot_recovery_t194.bin; \
        mts_preboot preboot_c10_prod_cr.bin; \
        mts_mce mce_c10_prod_cr.bin; \
        mts_proper mts_c10_prod_cr.bin; \
        bpmp_fw bpmp_t194.bin; \
        bpmp_fw_dtb tegra194-a02-bpmp-p3668-a00.dtb; \
        spe_fw spe_t194.bin; \
        tlk tos-trusty_t194.img; \
        eks eks.img; \
        bootloader_dtb ${DTBFILE}"

    cat flash.xml.in | sed \
        -e"s,LNXFILE,${localbootfile}," -e"s,LNXSIZE,${LNXSIZE}," \
        -e"s,TEGRABOOT,nvtboot_t194.bin," \
        -e"s,MTSPREBOOT,preboot_c10_prod_cr.bin," \
        -e"s,MTS_MCE,mce_c10_prod_cr.bin," \
        -e"s,MTSPROPER,mts_c10_prod_cr.bin," \
        -e"s,MB1FILE,mb1_t194_prod.bin," \
        -e"s,BPFFILE,bpmp_t194.bin," \
        -e"s,BPFDTB_FILE,tegra194-a02-bpmp-p3668-a00.dtb," \
        -e"s,TBCFILE,$cbootfilename," \
        -e"s,TBCDTB-FILE,${DTBFILE}," \
        -e"s,CAMERAFW,camera-rtcpu-rce.img," \
        -e"s,SPEFILE,spe_t194.bin," \
	-e"s,VERFILE,bsp_version," \
        -e"s,WB0BOOT,warmboot_t194_prod.bin," \
        -e"s,TOSFILE,tos-trusty_t194.img," \
        -e"s,EKSFILE,eks.img," \
        -e"s, DTB_FILE, ${DTBFILE}," \
        -e"s,CBOOTOPTION_FILE,cbo.dtb," \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,66060288," -e"s,RECDTB-NAME,recovery-dtb," -e"s,BOOTCTRLNAME,kernel-bootctrl," \
        -e"/RECFILE/d" -e"/RECDTB-FILE/d" -e"/BOOTCTRL-FILE/d" \
        > $destdir/flash.xml


     tegraflashpy=$(which tegraflash.py)

     python3 $tegraflashpy --bl nvtboot_recovery_cpu_t194.bin \
     --sdram_config tegra194-mb1-bct-memcfg-p3668-0001-a00.cfg,tegra194-memcfg-sw-override.cfg  \
     --odmdata 0xB8190000 \
     --applet mb1_t194_prod.bin \
     --cmd "sign$1" \
     --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg  \
     --cfg flash.xml \
     --chip 0x19 \
     --device_config tegra19x-mb1-bct-device-qspi-p3668.cfg \
     --misc_cold_boot_config tegra194-mb1-bct-misc-l4t.cfg \
     --misc_config tegra194-mb1-bct-misc-flash.cfg \
     --pinmux_config tegra19x-mb1-pinmux-p3668-a01.cfg \
     --gpioint_config tegra194-mb1-bct-gpioint-p3668-0001-a00.cfg \
     --pmic_config tegra194-mb1-bct-pmic-p3668-0001-a00.cfg \
     --pmc_config tegra19x-mb1-padvoltage-p3668-a01.cfg \
     --prod_config tegra19x-mb1-prod-p3668-0001-a00.cfg \
     --scr_config tegra194-mb1-bct-scr-cbb-mini-p3668.cfg \
     --scr_cold_boot_config tegra194-mb1-bct-scr-cbb-mini-p3668.cfg \
     --br_cmd_config tegra194-mb1-bct-reset-p3668-0001-a00.cfg \
     --dev_params tegra194-br-bct-qspi.cfg \
     --bin "${bins}"
}

do_configure() {
    local destdir="${WORKDIR}/tegraflash"
    local lnxfile="${LNXFILE}"
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -sf "${STAGING_DATADIR}/tegraflash/bsp_version" .
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}-override.cfg" .
    ln -s "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./cboot_t194.bin
    ln -s "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./tos-trusty_t194.img

    cp "${DEPLOY_DIR_IMAGE}/bootlogo-${MACHINE}.blob" ./bmp.blob
    mkdir -p ${DEPLOY_DIR_IMAGE}/bootfiles
    cp ./cboot_t194.bin ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp ./bmp.blob ${DEPLOY_DIR_IMAGE}/bootfiles/

    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
        cp "${STAGING_DATADIR}/tegraflash/$f" ${DEPLOY_DIR_IMAGE}/bootfiles/
    done

    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    . ./flashvars

    for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
        ln -s $f .
        cp $f ${DEPLOY_DIR_IMAGE}/bootfiles/
    done

    for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
        ln -s $f .
        cp $f ${DEPLOY_DIR_IMAGE}/bootfiles/
    done

    if [ -n "${NVIDIA_BOARD_CFG}" ]; then
        ln -s "${STAGING_DATADIR}/tegraflash/board_config_${MACHINE}.xml" .
        boardcfg=board_config_${MACHINE}.xml
    else
        boardcfg=
    fi
    export boardcfg

    sed -i -e "s/\[DTBNAME\]/${DTBNAME}/g" ${WORKDIR}/partition_specification194_nxde.txt

    ln -s ${STAGING_BINDIR_NATIVE}/tegra186-flash .

    cp "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}

    # This one is used to ensure carrier boards have
    # the same bldtb, so that the generated boot0.img
    # has valid signatures.
    cp "${DEPLOY_DIR_IMAGE}/tegra194-p3668-all-p3509-0000.dtb" .

    # These reside on the eMMC, can differ from bldtb
    cp ./${DTBFILE} ./${DTBNAME}-rootA.dtb
    cp ./${DTBFILE} ./${DTBNAME}-rootB.dtb

    # Add rootA/rootB and save as separate dtbs to be used when
    # switching partitions
    bootargs="`fdtget ./${DTBFILE} /chosen bootargs 2>/dev/null`"
    fdtput -t s ./${DTBNAME}-rootA.dtb /chosen bootargs "$bootargs ${ROOTA_ARGS} "
    fdtput -t s ./${DTBNAME}-rootB.dtb /chosen bootargs "$bootargs ${ROOTB_ARGS} "

    # Need to switch back to default values from flashing, otherwise bootloader dtb offset inside boot0.img will
    # change and will generate signature failure in MB2.
    bldtbchosenargs="console=ttyTCU0,115200"
    bldtbdtsname="/dvs/git/dirty/git-master_linux/kernel/kernel-4.9/arch/arm64/boot/dts/../../../../../../hardware/nvidia/platform/t19x/jakku/kernel-dts/tegra194-p3668-all-p3509-0000.dts"

    # Do not overide this hardcoded dtb for carrier boards, this is used for bldtb in boot0.img
    fdtput -t s ./tegra194-p3668-all-p3509-0000.dtb / "nvidia,dtsfilename" $bldtbdtsname
    fdtput -t s ./tegra194-p3668-all-p3509-0000.dtb /chosen bootargs $bldtbchosenargs

    # Make bootable image from kernel and sign it
    cp ${DEPLOY_DIR_IMAGE}/${LNXFILE} ${LNXFILE}
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg ./

    # mkbootimg really needs initrd, even if empty
    touch initrd
    ./mkbootimg --kernel ${LNXFILE} --ramdisk initrd --board mmcblk0p1 --output boot.img

    # prepare flash.xml.in to be used in signing
    cp ${WORKDIR}/${FLASHXML} flash.xml.in

    # prep env for tegraflash
    rm -f ./slot_metadata.bin
    cp ${STAGING_DATADIR}/tegraflash/slot_metadata.bin ./
    mkdir ./rollback

    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/rollback_parser.py ./rollback/
    ln -snf ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/BUP_generator.py ./
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/${SOC_FAMILY}-flash-helper.sh ./
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/tegraflash.py ./

    # bup is based on the rootfs, which is not built at this point
    # not using it for the moment
    # sed -e 's,^function ,,' ${STAGING_BINDIR_NATIVE}/tegra186-flash/l4t_bup_gen.func > ./l4t_bup_gen.func
    rm -rf signed

    # Sign all tegra bins
    signfile

    signfile " ${DTBNAME}-rootA.dtb"
    signfile " ${DTBNAME}-rootB.dtb"

    # Used in boot0.img
    signfile " tegra194-p3668-all-p3509-0000.dtb"

    # Needed to embedd plain initramfs kernel and dtb to main image
    cp $localbootfile ${DEPLOY_DIR_IMAGE}/bootfiles/Image

    cp -r ${DTBNAME}-root*.dtb* ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp ${WORKDIR}/${FLASHXML} ${DEPLOY_DIR_IMAGE}/bootfiles/flash.xml
    cp -r signed/* ${DEPLOY_DIR_IMAGE}/bootfiles/
    cp tegra194-p3668-all-p3509-0000_sigheader.dtb.encrypt ${DEPLOY_DIR_IMAGE}/bootfiles/

    dd if=/dev/zero count=1 bs=33554432 | tr "\000" "\377" > boot0.img
    dd if=/dev/zero bs=2887 count=1 of=boot0.img conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/br_bct_BR.bct of=boot0.img conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/br_bct_BR.bct of=boot0.img seek=4096 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/br_bct_BR.bct of=boot0.img seek=32768 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/br_bct_BR.bct of=boot0.img seek=65536 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/br_bct_BR.bct of=boot0.img seek=98304 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mb1_t194_prod_sigheader.bin.encrypt of=boot0.img seek=131072 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mb1_t194_prod_sigheader.bin.encrypt of=boot0.img seek=393216 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mb1_cold_boot_bct_MB1_sigheader.bct.encrypt of=boot0.img seek=655360 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mb1_cold_boot_bct_MB1_sigheader.bct.encrypt of=boot0.img seek=720896 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mem_coldboot_sigheader.bct.encrypt of=boot0.img seek=786432 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mem_coldboot_sigheader.bct.encrypt of=boot0.img seek=1048576 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/spe_t194_sigheader.bin.encrypt of=boot0.img seek=1310720 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/spe_t194_sigheader.bin.encrypt of=boot0.img seek=1572864 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/nvtboot_t194_sigheader.bin.encrypt of=boot0.img seek=1835008 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/nvtboot_t194_sigheader.bin.encrypt of=boot0.img seek=2097152 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/preboot_c10_prod_cr_sigheader.bin.encrypt of=boot0.img seek=2359296 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/preboot_c10_prod_cr_sigheader.bin.encrypt of=boot0.img seek=2424832 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mce_c10_prod_cr_sigheader.bin.encrypt of=boot0.img seek=2490368 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mce_c10_prod_cr_sigheader.bin.encrypt of=boot0.img seek=2686976 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mts_c10_prod_cr_sigheader.bin.encrypt of=boot0.img seek=2883584 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/mts_c10_prod_cr_sigheader.bin.encrypt of=boot0.img seek=7077888 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/warmboot_t194_prod_sigheader.bin.encrypt of=boot0.img seek=11272192 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/warmboot_t194_prod_sigheader.bin.encrypt of=boot0.img seek=11403264 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/slot_metadata.bin of=boot0.img seek=11534336 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/slot_metadata.bin of=boot0.img seek=11599872 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/xusb_sil_rel_fw of=boot0.img seek=11665408 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/xusb_sil_rel_fw of=boot0.img seek=11862016 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/cboot_t194_sigheader.bin.encrypt of=boot0.img seek=12058624 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/cboot_t194_sigheader.bin.encrypt of=boot0.img seek=13500416 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tegra194-p3668-all-p3509-0000_sigheader.dtb.encrypt of=boot0.img seek=14942208 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tegra194-p3668-all-p3509-0000_sigheader.dtb.encrypt of=boot0.img seek=15400960 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/bmp.blob of=boot0.img seek=15859712 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/bmp.blob of=boot0.img seek=16056320 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tos-trusty_t194_sigheader.img.encrypt of=boot0.img seek=16252928 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tos-trusty_t194_sigheader.img.encrypt of=boot0.img seek=18874368 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/eks_sigheader.img.encrypt of=boot0.img seek=21495808 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/eks_sigheader.img.encrypt of=boot0.img seek=21561344 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/adsp-fw_sigheader.bin.encrypt of=boot0.img seek=21626880 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/adsp-fw_sigheader.bin.encrypt of=boot0.img seek=22675456 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/camera-rtcpu-rce_sigheader.img.encrypt of=boot0.img seek=23724032 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/camera-rtcpu-rce_sigheader.img.encrypt of=boot0.img seek=24772608 bs=1 conv=notrunc

    # sce-fw empty both a+b

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/bpmp_t194_sigheader.bin.encrypt of=boot0.img seek=27918336 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/bpmp_t194_sigheader.bin.encrypt of=boot0.img seek=29491200 bs=1 conv=notrunc

    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tegra194-a02-bpmp-p3668-a00_sigheader.dtb.encrypt of=boot0.img seek=31064064 bs=1 conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/bootfiles/tegra194-a02-bpmp-p3668-a00_sigheader.dtb.encrypt of=boot0.img seek=32112640 bs=1 conv=notrunc

    # boot option file is empty on a clean flashed board, will leave it here
    # for offset refference
    #dd if=${DEPLOY_DIR_IMAGE}/bootfiles/cbo.dtb seek=33161216 bs=1 conv=notrunc
    #dd if=${DEPLOY_DIR_IMAGE}/bootfiles/cbo.dtb seek=33226752 bs=1 conv=notrunc

    # For the 32.4.4 release, /opt/tegra-binaries/boot0.img MD5
    # should be 9e271a445f75dbf33fb467f1be6debde
    # even for carrier boards. If it is not, then board will not boot after HUP.
    cp ${WORKDIR}/${BOOT_BINDIFF} .
    dd if=${BOOT_BINDIFF} of=boot0.img seek=14942224 bs=1 count=32 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=14945200 skip=32  bs=1 count=80 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=15400976 skip=112  bs=1 count=32 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=15400992 skip=144  bs=1 count=32 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=15403952 skip=176  bs=1 count=80 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=16252944 skip=256  bs=1 count=32 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=16255920 skip=288  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=18874384 skip=292  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=18877360 skip=356  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=21495824 skip=360  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=21498800 skip=424  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=21561360 skip=428  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=21564336 skip=492  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=27918352 skip=496  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=27921328 skip=560  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=29491216 skip=564  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=29494192 skip=628  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=31064080 skip=632  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=31067056 skip=696  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=32112656 skip=700  bs=1 count=64 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=32115632 skip=764  bs=1 count=4 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=33292288 skip=768  bs=1 count=256 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=33357824 skip=1024 bs=1 count=256 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=33537536 skip=1280 bs=1 count=16896 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=14946816 skip=18176 bs=1 count=48 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=15101952 skip=18224 bs=1 count=128 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=15405568 skip=18352 bs=1 count=48 conv=notrunc
    dd if=${BOOT_BINDIFF} of=boot0.img seek=15560704 skip=18400 bs=1 count=128 conv=notrunc
}

do_install() {
    install -d ${D}/${BINARY_INSTALL_PATH}
    cp -r ${S}/tegraflash/signed/* ${D}/${BINARY_INSTALL_PATH}

    cp ${S}/tegraflash/${DTBNAME}-rootA.dtb ${D}/${BINARY_INSTALL_PATH}/
    cp ${WORKDIR}/partition_specification194_nxde.txt ${D}/${BINARY_INSTALL_PATH}/
    cp -r ${S}/tegraflash/${DTBNAME}-root*sigheader.dtb.encrypt ${D}/${BINARY_INSTALL_PATH}
    cp ${S}/tegraflash/boot0.img ${D}/${BINARY_INSTALL_PATH}
    # When generating image, this will be default dtb containing cmdline with root set to resin-rootA
    cp ${S}/tegraflash/${DTBNAME}-rootA_sigheader.dtb.encrypt ${DEPLOY_DIR_IMAGE}/bootfiles/${DTBNAME}_sigheader.dtb.encrypt
}

do_deploy() {
    rm -rf ${DEPLOYDIR}/$(basename ${BINARY_INSTALL_PATH})
    mkdir -p ${DEPLOYDIR}/$(basename ${BINARY_INSTALL_PATH})
    cp -r ${D}/${BINARY_INSTALL_PATH}/* ${DEPLOYDIR}/$(basename ${BINARY_INSTALL_PATH})
}

FILES_${PN} += "${BINARY_INSTALL_PATH}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Binaries copied to DEPLOY_DIR_IMAGE
# to be included in the boot partition
# need to be redeployed on each build
# as this path is not cached
do_install[nostamp] = "1"
do_deploy[nostamp] = "1"
do_configure[nostamp] = "1"

do_configure[depends] += " tegra-binaries:do_preconfigure"
do_configure[depends] += " virtual/kernel:do_deploy \
                           virtual/bootloader:do_deploy \
"
do_configure[depends] += " cboot:do_deploy"
do_configure[depends] += " tos-prebuilt:do_deploy"

do_install[depends] += " virtual/kernel:do_deploy"
do_populate_lic[depends] += "tegra-binaries:do_unpack"

addtask do_deploy before do_package after do_install

COMPATIBLE_MACHINE = "jetson-xavier-nx-devkit-emmc"
