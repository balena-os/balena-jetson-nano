PACKAGE_INSTALL_append = " tegra-firmware-xusb"
PACKAGE_INSTALL_append_tegra186 = " kernel-module-bcmdhd"

# Because of https://github.com/balena-os/balena-jetson/issues/90
# the TX1 does not have a 32.X release currently. Let's decrease
# the kernel size to allow for host extensions support for now.
#PACKAGE_INSTALL_append_jetson-tx1 = " kernel-module-bcmdhd"
PACKAGE_INSTALL_remove_jetson-tx1 = "tegra-firmware-xusb"
PACKAGE_INSTALL_append_jetson-xavier = " util-linux-blockdev initramfs-module-blockdev"
PACKAGE_INSTALL_append_jetson-xavier-nx-devkit-emmc = " util-linux-blockdev initramfs-module-blockdev"
PACKAGE_INSTALL_append_jetson-xavier-nx-devkit = " util-linux-blockdev initramfs-module-blockdev"
