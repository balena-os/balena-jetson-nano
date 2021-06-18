IMAGE_FSTYPES_append = " balenaos-img"

do_rootfs_balenaos-img_jetson-nano[depends] += " tegra210-flash:do_deploy \
                                                virtual/kernel:do_deploy \
"

do_rootfs_balenaos-img_jetson-nano-emmc[depends] += " tegra210-flash:do_deploy \
                                                virtual/kernel:do_deploy \
"

do_rootfs_balenaos-img_jetson-nano-2gb-devkit[depends] += " tegra210-flash:do_deploy \
                                                virtual/kernel:do_deploy \
"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE', True).split()[0])}"
DTBFILE_jetson-nano = "tegra210-p3448-0000-p3449-0000-b00.dtb"
DTBFILE_jetson-nano-emmc = "tegra210-p3448-0002-p3449-0000-b00.dtb"
DTBFILE_jetson-nano-2gb-devkit = "tegra210-p3448-0003-p3542-0000.dtb"
DTBFILE_jn30b-nano = "tegra210-p3448-0002-p3449-0000-b00-jn30b.dtb"
DTBFILE_photon-nano = "tegra210-nano-cti-NGX003.dtb"
DTBFILE_floyd-nano="tegra210-p3448-0002-p3449-0000-b00-floyd-nano.dtb"
DTBFILE_photon-xavier-nx = "tegra194-xavier-nx-cti-NGX003.dtb"
DTBNAME_cti-rogue-xavier="tegra194-agx-cti-AGX101"
DTBNAME_jetson-xavier="tegra194-p2888-0001-p2822-0000"
DTBNAME_jetson-xavier-nx-devkit-seeed-2mic-hat="tegra194-p3668-all-p3509-0000-seeed-2mic-hat"
DTBNAME_nru120s-xavier="NRU120-32-4-3"

# Name update files as partition names for easier updating
BALENA_BOOT_PARTITION_FILES_jetson-nano = " \
    bootfiles/nvtboot_cpu.bin.encrypt:/bootfiles/TBC \
    bootfiles/${DTBFILE}.encrypt:/bootfiles/RP1 \
    bootfiles/cboot.bin.encrypt:/bootfiles/EBT \
    bootfiles/warmboot.bin.encrypt:/bootfiles/WB0 \
    bootfiles/sc7entry-firmware.bin.encrypt:/bootfiles/BPF \
    bootfiles/tos-mon-only.img.encrypt:/bootfiles/TOS \
    bootfiles/eks.img:/bootfiles/EKS \
    bootfiles/u-boot.bin.encrypt:/bootfiles/LNX \
    bootfiles/${DTBFILE}.encrypt:/bootfiles/DTB \
    bootfiles/rp4.blob:/bootfiles/RP4 \
    bootfiles/bmp.blob:/bootfiles/BMP \
"

BALENA_BOOT_PARTITION_FILES_jetson-nano-emmc = "${BALENA_BOOT_PARTITION_FILES_jetson-nano}"
BALENA_BOOT_PARTITION_FILES_jetson-nano-2gb-devkit = "${BALENA_BOOT_PARTITION_FILES_jetson-nano}"


do_rootfs_balenaos-img_jetson-xavier[depends] += " tegra194-flash-dry:do_deploy "

BALENA_BOOT_PARTITION_FILES_jetson-xavier = " \
    bootfiles/mb1_t194_prod_sigheader.bin.encrypt:/bootfiles/mb1_t194_prod_sigheader.bin.encrypt \
    bootfiles/spe_t194_sigheader.bin.encrypt:/bootfiles/spe_t194_sigheader.bin.encrypt \
    bootfiles/nvtboot_t194_sigheader.bin.encrypt:/bootfiles/nvtboot_t194_sigheader.bin.encrypt \
    bootfiles/preboot_c10_prod_cr_sigheader.bin.encrypt:/bootfiles/preboot_c10_prod_cr_sigheader.bin.encrypt \
    bootfiles/slot_metadata.bin:/bootfiles/slot_metadata.bin \
    bootfiles/bmp.blob:/bootfiles/bmp.blob \
    bootfiles/mce_c10_prod_cr_sigheader.bin.encrypt:/bootfiles/mce_c10_prod_cr_sigheader.bin.encrypt \
    bootfiles/mts_c10_prod_cr_sigheader.bin.encrypt:/bootfiles/mts_c10_prod_cr_sigheader.bin.encrypt \
    bootfiles/cboot_t194_sigheader.bin.encrypt:/bootfiles/cboot_t194_sigheader.bin.encrypt \
    bootfiles/tos-trusty_t194_sigheader.img.encrypt:/bootfiles/tos-trusty_t194_sigheader.img.encrypt \
    bootfiles/eks_sigheader.img.encrypt:/bootfiles/eks_sigheader.img.encrypt \
    bootfiles/bpmp_t194_sigheader.bin.encrypt:/bootfiles/bpmp_t194_sigheader.bin.encrypt \
    bootfiles/tegra194-a02-bpmp-p2888-a04_sigheader.dtb.encrypt:/bootfiles/tegra194-a02-bpmp-p2888-a04_sigheader.dtb.encrypt \
    bootfiles/xusb_sil_rel_fw:/bootfiles/xusb_sil_rel_fw \
    bootfiles/camera-rtcpu-rce_sigheader.img.encrypt:/bootfiles/camera-rtcpu-rce_sigheader.img.encrypt \
    bootfiles/adsp-fw_sigheader.bin.encrypt:/bootfiles/adsp-fw_sigheader.bin.encrypt \
    bootfiles/warmboot_t194_prod_sigheader.bin.encrypt:/bootfiles/warmboot_t194_prod_sigheader.bin.encrypt \
    bootfiles/boot_sigheader.img.encrypt:/bootfiles/boot_sigheader.img.encrypt \
    bootfiles/${DTBNAME}-rootA_sigheader.dtb.encrypt:/bootfiles/${DTBNAME}-rootA_sigheader.dtb.encrypt \
    bootfiles/${DTBNAME}-rootB_sigheader.dtb.encrypt:/bootfiles/${DTBNAME}-rootB_sigheader.dtb.encrypt \
    bootfiles/boot0.img:/bootfiles/boot0.img \
    tegra-binaries/partition_specification194.txt:/bootfiles/partition_specification194.txt \
"

do_rootfs_balenaos-img_jetson-tx2[depends] += " tegra186-flash-dry:do_deploy \
                                               virtual/kernel:do_deploy \
"

BALENA_BOOT_PARTITION_FILES_jetson-tx2 = " \
    boot/extlinux.conf:/extlinux/extlinux.conf \
"

BALENA_BOOT_PARTITION_FILES_jetson-xavier-nx-devkit-emmc = " \
    boot/extlinux.conf:/extlinux/extlinux.conf \
"

BALENA_BOOT_PARTITION_FILES_jetson-xavier-nx-devkit = " \
    boot/extlinux.conf:/extlinux/extlinux.conf \
"

# Keep these in boot also, for a plain u-boot
# to boot a flasher image.
BALENA_BOOT_PARTITION_FILES_jetson-tx1 = " \
    ${KERNEL_IMAGETYPE}${KERNEL_INITRAMFS}-${MACHINE}.bin:/${KERNEL_IMAGETYPE} \
    boot/extlinux.conf:/extlinux/extlinux.conf \
"

do_rootfs_balenaos-img_jetson-xavier-nx-devkit-emmc[depends] += " tegra194-nxde-flash-dry:do_deploy "
do_rootfs_balenaos-img_jetson-xavier-nx-devkit[depends] += " tegra194-nxde-sdcard-flash:do_deploy "

# The space resinOS takes will amount to 1GiB
IMAGE_ROOTFS_SIZE = "487424"

# Bootloader blob is 32MB on the NX
IMAGE_ROOTFS_SIZE_jetson-xavier-nx-devkit-emmc = "733184"
IMAGE_ROOTFS_SIZE_jetson-xavier-nx-devkit = "733184"

# Need space for all the above signed update binaries
BALENA_BOOT_SIZE_jetson-nano = "80960"
BALENA_BOOT_SIZE_jetson-nano-2gb-devkit = "80960"
BALENA_BOOT_SIZE_jetson-nano-emmc = "80960"
BALENA_BOOT_SIZE_jetson-xavier = "121440"
BALENA_BOOT_SIZE_jetson-xavier-nx-devkit-emmc = "121440"
BALENA_BOOT_SIZE_jetson-xavier-nx-devkit = "121440"
BALENA_BOOT_SIZE_jetson-tx2="80960"
BALENA_BOOT_SIZE_jetson-tx1="80960"

PARTITION_TABLE_TYPE = "gpt"

IMAGE_PACKAGES_NANO = " \
    tegra210-flash \
    tegra-nvpmodel \
    tegra-configs-nvstartup \
    tegra-configs-udev \
    jetson-dtbs \
    mtd-utils \
"

IMAGE_INSTALL_append_jetson-nano = "${IMAGE_PACKAGES_NANO}"
IMAGE_INSTALL_append_jetson-nano-emmc = "${IMAGE_PACKAGES_NANO}"
IMAGE_INSTALL_append_jetson-nano-2gb-devkit = "${IMAGE_PACKAGES_NANO}"

IMAGE_INSTALL_append_jetson-xavier = " \
    tegra194-flash-dry \
    fan-startup \
    parted \
    gptfdisk \
    tegra-nvpmodel \
    tegra-configs-nvstartup \
    tegra-configs-udev \
"

IMAGE_INSTALL_append_jetson-xavier-nx-devkit-emmc = " \
    tegra194-nxde-flash-dry \
    parted \
    gptfdisk \
    tegra-nvpmodel \
    tegra-configs-nvstartup \
    tegra-configs-udev \
    mtd-utils \
"

IMAGE_INSTALL_append_jetson-xavier-nx-devkit = " \
    tegra194-nxde-sdcard-flash \
    fan-startup \
    parted \
    gptfdisk \
    tegra-nvpmodel \
    tegra-configs-nvstartup \
    tegra-configs-udev \
    mtd-utils \
    tegra-bluetooth \
    tegra-wifi \
    tegra-firmware-rtl8822 \
    tegra-udrm-probeconf \
"

IMAGE_INSTALL_append_jetson-tx2 = " \
    tegra186-flash-dry \
    parted \
    gptfdisk \
    fan-startup \
    tegra-nvpmodel \
    tegra-configs-nvstartup \
    tegra-configs-udev \
    tegra-firmware-brcm \
    tegra-brcm-patchram \
    linux-firmware-bcm4354 \
    tegra-firmware-xusb \
    bt-scripts \
    jetson-dtbs \
"
IMAGE_INSTALL_append_jetson-tx2-nx-devkit = " \
    tegra-firmware-rtl8822 \
    kernel-module-rtk-btusb \
    kernel-module-rtl8822ce \
"

IMAGE_INSTALL_append_jetson-tx1 = " \
    tegra210-flash-dry \
    parted \
    tegra-nvpmodel \
    tegra-configs-nvstartup \
    tegra-configs-udev \
    tegra-firmware-brcm \
    tegra-brcm-patchram \
    linux-firmware-bcm4354 \
    bt-scripts \
"
