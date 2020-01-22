inherit kernel-resin deploy

FILESEXTRAPATHS_append := ":${THISDIR}/${PN}"

SCMVERSION="n"

# Prevent delayed booting
# and support using partition label to load rootfs
# in the case of jetson-xavier and tx2 flasher
SRC_URI_append = " \
    file://0001-revert-random-fix-crng_ready-test.patch \
    file://0001-Support-referencing-the-root-partition-label-from-GP.patch \
"
SRC_URI_append_jetson-tx2 = " \
    file://0001-Expose-spidev-to-the-userspace.patch \
    file://0002-mttcan-ivc-enable.patch \
    file://tegra186-tx2-cti-ASG001-USB3.dtb \
    file://tegra186-quill-p3310-1000-c03-00-base.dtb \
    file://tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb \
    file://d3-tx2-rsp-fpdlink.dtb \
    file://tegra186-tx2-blackboard.dtb \
    file://realsense_hid_linux-yocto_4.4.patch \
    file://realsense_metadata_linux-yocto_4.4.patch \
    file://realsense_powerlinefrequency_control_fix_linux-yocto_4.4.patch \
    file://realsense_camera_formats_linux-yocto_4.4.patch \
    file://realsense_format_desc_4.4.patch \
    file://0002-qmi_wwan-Update-from-4.14-kernel.patch \
    file://0002-NFLX-2019-001-SACK-Panic.patch \
    file://0003-NFLX-2019-001-SACK-Panic-for-lteq-4.14.patch \
    file://0004-NFLX-2019-001-SACK-Slowness.patch \
    file://0005-NFLX-2019-001-Resour-Consump-Low-MSS.patch \
    file://0006-NFLX-2019-001-Resour-Consump-Low-MSS.patch \
    file://0001-mttcan_ivc-Fix-build-failure-with-kernel-4.9.patch \
"

SRC_URI_append_jn30b-nano = " \
    file://tegra210-p3448-0002-p3449-0000-b00-jn30b-JP4.2.2.dtb \
"

SRC_URI_append_m2pcie-tx2 = " \
    file://0001-Add-gasket-module-1.1.3.patch \
"

TEGRA_INITRAMFS_INITRD = "0"

RESIN_CONFIGS_append = " tegra-wdt-t21x debug_kmemleak "

RESIN_CONFIGS[tegra-wdt-t21x] = " \
    CONFIG_TEGRA21X_WATCHDOG=m \
"

RESIN_CONFIGS[debug_kmemleak] = " \
    CONFIG_HAVE_DEBUG_KMEMLEAK=n \
    CONFIG_DEBUG_KMEMLEAK=n \
    CONFIG_HAVE_DEBUG_KMEMLEAK=n \
    CONFIG_DEBUG_KMEMLEAK_DEFAULT_OFF=y \
    CONFIG_DEBUG_KMEMLEAK_SCAN_ON=n \
"

# These should be for all boards that come from tx2
RESIN_CONFIGS_append_jetson-tx2 = " tpg"
RESIN_CONFIGS[tpg] = " \
                CONFIG_VIDEO_TEGRA_VI_TPG=m \
"

RESIN_CONFIGS_append_jetson-tx2 = " compat"
RESIN_CONFIGS[compat] = " \
                CONFIG_COMPAT=y \
"

RESIN_CONFIGS_append_jetson-tx2 = " uvc"
RESIN_CONFIGS[uvc] = " \
                CONFIG_USB_VIDEO_CLASS=m \
                CONFIG_USB_VIDEO_CLASS_INPUT_EVDEV=y \
                "

RESIN_CONFIGS_DEPS[uvc] = " \
                CONFIG_MEDIA_CAMERA_SUPPORT=y \
                CONFIG_VIDEO_V4L2_SUBDEV_API=y \
                CONFIG_VIDEO_V4L2=m \
                CONFIG_VIDEOBUF2_CORE=m \
                CONFIG_VIDEOBUF2_MEMOPS=m \
                CONFIG_VIDEOBUF2_VMALLOC=m \
                CONFIG_MEDIA_USB_SUPPORT=y \
                CONFIG_USB_GSPCA=m \
                CONFIG_SND_USB=y \
                CONFIG_SND_USB_AUDIO=m \
                "

RESIN_CONFIGS_append_jetson-tx2 = " egalax"
RESIN_CONFIGS[egalax] = " \
                CONFIG_TOUCHSCREEN_EGALAX=m \
                "

RESIN_CONFIGS_append_jetson-tx2 = " serial"
RESIN_CONFIGS[serial] = " \
                CONFIG_USB_SERIAL_GENERIC=y \
                "

RESIN_CONFIGS_append_jetson-tx2 = " spi"
RESIN_CONFIGS[spi] = " \
                CONFIG_SPI=y \
                CONFIG_SPI_MASTER=y \
                CONFIG_SPI_SPIDEV=m \
                "
RESIN_CONFIGS_DEPS[spi] = " \
                CONFIG_QSPI_TEGRA186=y \
                CONFIG_SPI_TEGRA144=y \
                "

RESIN_CONFIGS_append_jetson-tx2 = " gamepad"
RESIN_CONFIGS[gamepad] = " \
                CONFIG_JOYSTICK_XPAD=m \
                "
RESIN_CONFIGS_DEPS[gamepad] = " \
                CONFIG_INPUT_JOYSTICK=y \
                CONFIG_USB_ARCH_HAS_HCD=y \
                "

RESIN_CONFIGS_append_jetson-tx2 = " can"
RESIN_CONFIGS[can] = " \
                CONFIG_CAN=m \
                CONFIG_CAN_RAW=m \
                CONFIG_CAN_DEV=m \
                CONFIG_MTTCAN=m \
                CONFIG_MTTCAN_IVC=m \
"

RESIN_CONFIGS_append_m2pcie-tx2 = " gasket"
RESIN_CONFIGS[gasket] = " \
        CONFIG_STAGING_GASKET_FRAMEWORK=m \
        CONFIG_STAGING_APEX_DRIVER=m \
        "

RESIN_CONFIGS_append_srd3-tx2 = " tpg"

KERNEL_MODULE_AUTOLOAD_srd3-tx2 += " nvhost-vi-tpg "
KERNEL_MODULE_PROBECONF_srd3-tx2 += " nvhost-vi-tpg tegra-udrm"

KERNEL_ROOTSPEC_jetson-nano = "\${resin_kernel_root} ro rootwait"
KERNEL_ROOTSPEC_jetson-tx2 = " \${resin_kernel_root} ro rootwait sdhci_tegra.en_boot_part_access=1"
KERNEL_ROOTSPEC_jetson-xavier = ""

# Since 32.1 on tx2, after kernel is loaded sd card becomes mmcblk2 opposed
# to u-boot where it was 1. This is another cause of failure of
# previous flasher images.  Use label to distinguish rootfs
KERNEL_ROOTSPEC_FLASHER_jetson-tx2 = " root=LABEL=flash-rootA ro rootwait sdhci_tegra.en_boot_part_access=1 flasher"

KERNEL_ROOTSPEC_append_m2pcie-tx2 = " gasket.dma_bit_mask=32 pcie_aspm=off"
KERNEL_ROOTSPEC_FLASHER_append_m2pcie-tx2 = " gasket.dma_bit_mask=32 pcie_aspm=off"

generate_extlinux_conf() {
    install -d ${D}/${KERNEL_IMAGEDEST}/extlinux
    rm -f ${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf
    kernelRootspec="${KERNEL_ROOTSPEC}" ; cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
DEFAULT primary
TIMEOUT 10
MENU TITLE Boot Options
LABEL primary
      MENU LABEL primary ${KERNEL_IMAGETYPE}
      LINUX /${KERNEL_IMAGETYPE}
      APPEND \${cbootargs} ${kernelRootspec} \${os_cmdline}
EOF
    kernelRootspec="${KERNEL_ROOTSPEC_FLASHER}" ; cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf_flasher << EOF
DEFAULT primary
TIMEOUT 10
MENU TITLE Boot Options
LABEL primary
      MENU LABEL primary ${KERNEL_IMAGETYPE}
      LINUX /${KERNEL_IMAGETYPE}
      APPEND ${KERNEL_ARGS} ${kernelRootspec} \${os_cmdline}
EOF
}

do_install[postfuncs] += "generate_extlinux_conf"
do_install[depends] += "${@['', '${INITRAMFS_IMAGE}:do_image_complete'][(d.getVar('INITRAMFS_IMAGE', True) or '') != '' and (d.getVar('TEGRA_INITRAMFS_INITRD', True) or '') == "1"]}"

do_deploy_append(){
    mkdir -p "${DEPLOYDIR}/boot/"
    install -m 0600 "${D}/boot/extlinux/extlinux.conf" "${DEPLOYDIR}/boot/"
    install -m 0600 "${D}/boot/extlinux/extlinux.conf_flasher" "${DEPLOYDIR}/boot/"
}

FILES_${KERNEL_PACKAGE_NAME}-image_append = "/boot/extlinux/extlinux.conf /boot/extlinux/extlinux.conf_flasher"

do_deploy_append_spacely-tx2() {
   cp ${WORKDIR}/tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb "${DEPLOYDIR}"
}

do_deploy_append_orbitty-tx2() {
   cp ${WORKDIR}/tegra186-tx2-cti-ASG001-USB3.dtb "${DEPLOYDIR}"
}

do_deploy_append_n510-tx2() {
    cp ${WORKDIR}/tegra186-quill-p3310-1000-c03-00-base.dtb "${DEPLOYDIR}"
}

do_deploy_append_srd3-tx2() {
    cp ${WORKDIR}/d3-tx2-rsp-fpdlink.dtb "${DEPLOYDIR}"
}

do_deploy_append_blackboard-tx2() {
    cp ${WORKDIR}/tegra186-tx2-blackboard.dtb "${DEPLOYDIR}"
}

do_deploy_append_jn30b-nano() {
    cp ${WORKDIR}/tegra210-p3448-0002-p3449-0000-b00-jn30b-JP4.2.2.dtb "${DEPLOYDIR}"
}
