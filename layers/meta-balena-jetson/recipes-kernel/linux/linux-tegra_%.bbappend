inherit kernel-resin deploy

FILESEXTRAPATHS_append := ":${THISDIR}/${PN}"

SCMVERSION="n"

# We pin to the 32.4.4 revision as of 12 Jan 2021
# to ensure the upstream BSP layer doesn't bring in a newer
# version that might fail to build or boot without notice.
SRCREV = "87e09c14b15ad302b451f40f4237bb14f553c1e0"

# Prevent delayed booting
# and support using partition label to load rootfs
# in the case of jetson-xavier and tx2 flasher
SRC_URI_append = " \
    file://0001-revert-random-fix-crng_ready-test.patch \
    file://0001-Support-referencing-the-root-partition-label-from-GP.patch \
    file://xhci-ring-Don-t-show-incorrect-WARN-message-about.patch \
"

# Patches pulled from upstream for crashkernel memory reservation
# c9ca9b4..764b51e (last patch required minor modifications)
SRC_URI_append = " \
    file://0001-memblock-add-memblock_cap_memory_range.patch \
    file://0002-arm64-limit-memory-regions-based-on-DT-property-usab.patch \
    file://0003-arm64-kdump-reserve-memory-for-crash-dump-kernel.patch \
    "

SRC_URI_append_jetson-tx2 = " \
    file://0001-Expose-spidev-to-the-userspace.patch \
    file://0002-mttcan-ivc-enable.patch \
    file://tegra186-tx2-cti-ASG001-USB3.dtb \
    file://tegra186-tx2-aetina-n510-p3489-0888-a00-00-base.dtb \
    file://tegra186-tx2-aetina-n310-p3489-0888-a00-00-base.dtb \
    file://tegra186-tx2-cti-ASG006-IMX274-6CAM.dtb \
    file://tegra186-tx2-blackboard.dtb \
    file://realsense_powerlinefrequency_control_fix_linux-yocto_4.4.patch \
    file://0002-qmi_wwan-Update-from-4.14-kernel.patch \
    file://0001-mttcan_ivc-Fix-build-failure-with-kernel-4.9.patch \
    file://0001-gasket-Backport-gasket-driver-from-linux-coral.patch \
"

SRC_URI_append_cti-rogue-xavier = " \
    file://tegra194-agx-cti-AGX101.dtb \
"

SRC_URI_append_nru120s-xavier = " \
    file://NRU120-32-4-3.dtb \
"

SRC_URI_append_astro-tx2 = " \
    file://tegra186-tx2-cti-ASG001-revG+.dtb \
"

SRC_URI_append_jn30b-nano = " \
    file://tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb \
"
SRC_URI_append_floyd-nano = " \
    file://floyd-nano-Port-patches-from-L4T-32.3.1-for-this-DT.patch \
"

SRC_URI_append_jetson-nano = " \
    file://0001-gasket-Backport-gasket-driver-from-linux-coral.patch \
    file://0001-Enable-SPI1.patch \
"

SRC_URI_append_jetson-nano-emmc = " \
    file://nano-mark-gpio-as-disabled-when-freed.patch \
    file://0001-gasket-Backport-gasket-driver-from-linux-coral.patch \
"

SRC_URI_append_photon-nano = " \
    file://0001-cti-photon-merge-CDC-MBIM-driver-changes-from-bsp.patch \
    file://tegra210-nano-cti-NGX003.dtb \
"

SRC_URI_append_photon-xavier-nx = " \
    file://0001-cti-photon-merge-CDC-MBIM-driver-changes-from-bsp.patch \
    file://tegra194-xavier-nx-cti-NGX003.dtb \
"

TEGRA_INITRAMFS_INITRD = "0"

BALENA_CONFIGS_append = " kdump"

BALENA_CONFIGS[kdump] = " \
    CONFIG_KEXEC=y \
    CONFIG_SYSFS=y \
    "

BALENA_CONFIGS_append = " tegra-wdt-t21x debug_kmemleak "

BALENA_CONFIGS[tegra-wdt-t21x] = " \
    CONFIG_TEGRA21X_WATCHDOG=m \
"

BALENA_CONFIGS[debug_kmemleak] = " \
    CONFIG_HAVE_DEBUG_KMEMLEAK=n \
    CONFIG_DEBUG_KMEMLEAK=n \
    CONFIG_HAVE_DEBUG_KMEMLEAK=n \
    CONFIG_DEBUG_KMEMLEAK_DEFAULT_OFF=y \
    CONFIG_DEBUG_KMEMLEAK_SCAN_ON=n \
"

# These should be for all boards that come from tx2
BALENA_CONFIGS_append_jetson-tx2 = " tpg eqos_disable_eee"
BALENA_CONFIGS[tpg] = " \
                CONFIG_VIDEO_TEGRA_VI_TPG=m \
"

# Fixes reported ethernet issues
# See: elinux.org/Jetson/FAQ/BSP
BALENA_CONFIGS[eqos_disable_eee] = " \
                CONFIG_EQOS_DISABLE_EEE=y \
"

BALENA_CONFIGS_append_jetson-tx1 = " compat"
BALENA_CONFIGS_append_jetson-tx2 = " compat"
BALENA_CONFIGS[compat] = " \
                CONFIG_COMPAT=y \
"

BALENA_CONFIGS_remove_jetson-tx1 = " brcmfmac"
BALENA_CONFIGS_append_jetson-tx2 = " uvc"
BALENA_CONFIGS[uvc] = " \
                CONFIG_USB_VIDEO_CLASS=m \
                CONFIG_USB_VIDEO_CLASS_INPUT_EVDEV=y \
"

BALENA_CONFIGS_DEPS[uvc] = " \
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

BALENA_CONFIGS_append_jetson-tx2 = " egalax"
BALENA_CONFIGS[egalax] = " \
                CONFIG_TOUCHSCREEN_EGALAX=m \
"

BALENA_CONFIGS_append_jetson-tx2 = " serial"
BALENA_CONFIGS[serial] = " \
                CONFIG_USB_SERIAL_GENERIC=y \
"

BALENA_CONFIGS_append_jetson-tx2 = " spi"
BALENA_CONFIGS[spi] = " \
                CONFIG_SPI=y \
                CONFIG_SPI_MASTER=y \
                CONFIG_SPI_SPIDEV=m \
"
BALENA_CONFIGS_DEPS[spi] = " \
                CONFIG_QSPI_TEGRA186=y \
                CONFIG_SPI_TEGRA144=y \
"

BALENA_CONFIGS_append_jetson-tx2 = " gamepad"
BALENA_CONFIGS[gamepad] = " \
                CONFIG_JOYSTICK_XPAD=m \
"
BALENA_CONFIGS_DEPS[gamepad] = " \
                CONFIG_INPUT_JOYSTICK=y \
                CONFIG_USB_ARCH_HAS_HCD=y \
"

BALENA_CONFIGS_append_jetson-tx2 = " can"
BALENA_CONFIGS[can] = " \
                CONFIG_CAN=m \
                CONFIG_CAN_RAW=m \
                CONFIG_CAN_DEV=m \
                CONFIG_MTTCAN=m \
                CONFIG_MTTCAN_IVC=m \
"

BALENA_CONFIGS[d3_hdr] = " \
	CONFIG_D3_IMX390_HDR_ENABLE=y \
"

BALENA_CONFIGS_append_jetson-tx2 = " gasket"
BALENA_CONFIGS[gasket] = " \
        CONFIG_STAGING_GASKET_FRAMEWORK=m \
        CONFIG_STAGING_APEX_DRIVER=m \
"

BALENA_CONFIGS_append_jetson-nano = " gasket"
BALENA_CONFIGS_append_jetson-nano-emmc = " gasket"

BALENA_CONFIGS_append_photon-nano = " tlc591xx"
BALENA_CONFIGS_append_photon-xavier-nx = " tlc591xx"
BALENA_CONFIGS[tlc591xx] = " \
                CONFIG_LEDS_TLC591XX=m \
"

BALENA_CONFIGS_append_photon-nano = " cdc-wdm"
BALENA_CONFIGS_append_photon-xavier-nx = " cdc-wdm"
BALENA_CONFIGS[cdc-wdm] = " \
                CONFIG_USB_WDM=m \
"

BALENA_CONFIGS_append_photon-nano = " sierra-net"
BALENA_CONFIGS_append_photon-xavier-nx = " sierra-net"
BALENA_CONFIGS[sierra-net] = " \
                CONFIG_USB_SIERRA_NET=m \
"

BALENA_CONFIGS_DEPS[sierra-net] = " \
                CONFIG_USB_USBNET=m \
"

BALENA_CONFIGS_append_photon-nano = " cdc-ncm"
BALENA_CONFIGS_append_photon-xavier-nx = " cdc-ncm"
BALENA_CONFIGS[cdc-ncm] = " \
                CONFIG_USB_NET_CDC_NCM=m \
"

BALENA_CONFIGS_DEPS[cdc-ncm] = " \
                CONFIG_USB_USBNET=m \
"

BALENA_CONFIGS_append_photon-nano = " mii"
BALENA_CONFIGS_append_photon-xavier-nx = " mii"
BALENA_CONFIGS[mii] = " \
                CONFIG_MII=m \
"

BALENA_CONFIGS_append = " cfginput"
BALENA_CONFIGS[cfginput] = " \
		CONFIG_INPUT_LEDS=m \
		CONFIG_FF_MEMLESS=m \
		CONFIG_INPUT_MOUSEDEV=m \
		CONFIG_INPUT_JOYDEV=m \
		CONFIG_JOYSTICK_XPAD=m \
		CONFIG_INPUT_KEYCHORD=m \
"

BALENA_CONFIGS_append_jetson-xavier-nx-devkit = " rtl8822ce "
BALENA_CONFIGS[rtl8822ce] = " \
		CONFIG_RTL8822CE=m \
		CONFIG_RTK_BTUSB=m \
"

# Switch nfs and backlight drivers as modules
# to shrink down the kernel image size starting
# with BalenaOS 2.65.0
BALENA_CONFIGS_append = " nfsfs backlight "
BALENA_CONFIGS[nfsfs] = " \
    CONFIG_NFS_FS=m \
    CONFIG_NFS_V2=m \
    CONFIG_NFS_V3=m \
"

BALENA_CONFIGS[backlight] = " \
    CONFIG_BACKLIGHT_PWM=m \
    CONFIG_BACKLIGHT_LP855X=m \
    CONFIG_BACKLIGHT_CLASS_DEVICE=m \
"

KERNEL_ROOTSPEC_jetson-nano = "\${resin_kernel_root} ro rootwait"
KERNEL_ROOTSPEC_jetson-nano-emmc = "\${resin_kernel_root} ro rootwait"
KERNEL_ROOTSPEC_jetson-nano-2gb-devkit = "\${resin_kernel_root} ro rootwait"
KERNEL_ROOTSPEC_jn30b-nano = "\${resin_kernel_root} ro rootwait"
KERNEL_ROOTSPEC_jetson-tx2 = " \${resin_kernel_root} ro rootwait gasket.dma_bit_mask=32 pcie_aspm=off"
KERNEL_ROOTSPEC_jetson-tx1 = " \${resin_kernel_root} ro rootwait"
KERNEL_ROOTSPEC_jetson-xavier = ""
KERNEL_ROOTSPEC_jetson-xavier-nx-devkit-emmc = ""

# Since 32.1 on tx2, after kernel is loaded sd card becomes mmcblk2 opposed
# to u-boot where it was 1. This is another cause of failure of
# previous flasher images.  Use label to distinguish rootfs
KERNEL_ROOTSPEC_FLASHER_jetson-tx2 = " root=LABEL=flash-rootA ro rootwait flasher gasket.dma_bit_mask=32 pcie_aspm=off"
KERNEL_ROOTSPEC_FLASHER_jetson-tx1 = " root=LABEL=flash-rootA ro rootwait flasher"

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
      FDT default
      APPEND \${cbootargs} ${kernelRootspec} \${os_cmdline} sdhci_tegra.en_boot_part_access=1
EOF
    kernelRootspec="${KERNEL_ROOTSPEC_FLASHER}" ; cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf_flasher << EOF
DEFAULT primary
TIMEOUT 10
MENU TITLE Boot Options
LABEL primary
      MENU LABEL primary ${KERNEL_IMAGETYPE}
      LINUX /${KERNEL_IMAGETYPE}
      APPEND \${cbootargs} ${kernelRootspec} \${os_cmdline} sdhci_tegra.en_boot_part_access=1
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
    cp ${WORKDIR}/tegra186-tx2-aetina-n510-p3489-0888-a00-00-base.dtb "${DEPLOYDIR}"
}
do_deploy_append_n310-tx2() {
    cp ${WORKDIR}/tegra186-tx2-aetina-n310-p3489-0888-a00-00-base.dtb "${DEPLOYDIR}"
}

do_deploy_append_blackboard-tx2() {
    cp ${WORKDIR}/tegra186-tx2-blackboard.dtb "${DEPLOYDIR}"
}

do_deploy_append_jn30b-nano() {
    cp ${WORKDIR}/tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb "${DEPLOYDIR}"
}

do_deploy_append_photon-nano() {
    cp ${WORKDIR}/tegra210-nano-cti-NGX003.dtb "${DEPLOYDIR}"
}

do_deploy_append_photon-xavier-nx() {
    cp ${WORKDIR}/tegra194-xavier-nx-cti-NGX003.dtb "${DEPLOYDIR}"
}

do_deploy_append_astro-tx2() {
    cp ${WORKDIR}/tegra186-tx2-cti-ASG001-revG+.dtb "${DEPLOYDIR}"
}

do_deploy_append_cti-rogue-xavier() {
    cp ${WORKDIR}/tegra194-agx-cti-AGX101.dtb "${DEPLOYDIR}"
}

do_deploy_append_nru120s-xavier() {
    cp ${WORKDIR}/NRU120-32-4-3.dtb "${DEPLOYDIR}"
}
