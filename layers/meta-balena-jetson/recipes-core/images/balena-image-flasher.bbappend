include balena-image.inc

BALENA_BOOT_PARTITION_FILES_append_tegra186 = " \
    ${KERNEL_IMAGETYPE}${KERNEL_INITRAMFS}-${MACHINE}.bin:/${KERNEL_IMAGETYPE} \
"

BALENA_BOOT_PARTITION_FILES_append_tegra186 = " \
    boot/extlinux.conf_flasher:/extlinux/extlinux.conf \
"
BALENA_BOOT_PARTITION_FILES_append_jetson-tx1 = " \
    boot/extlinux.conf_flasher:/extlinux/extlinux.conf \
"
