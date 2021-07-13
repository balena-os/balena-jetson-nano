PACKAGE_INSTALL_append = " tegra-firmware-xusb"
PACKAGE_INSTALL_append_jetson-tx2 = " kernel-module-bcmdhd"

# Because of https://github.com/balena-os/balena-jetson/issues/90
# the TX1 does not have a 32.X release currently. Let's decrease
# the kernel size to allow for host extensions support for now.
#PACKAGE_INSTALL_append_jetson-tx1 = " kernel-module-bcmdhd"
PACKAGE_INSTALL_remove_jetson-tx1 = "tegra-firmware-xusb"
PACKAGE_INSTALL_append_jetson-xavier = " util-linux-blockdev initramfs-module-blockdev"
PACKAGE_INSTALL_append_jetson-xavier-nx-devkit-emmc = " util-linux-blockdev initramfs-module-blockdev"
PACKAGE_INSTALL_append_jetson-xavier-nx-devkit = " util-linux-blockdev initramfs-module-blockdev"

PACKAGE_INSTALL_remove_astro-tx2 = " mdraid"
PACKAGE_INSTALL_remove_blackboard-tx2 = " mdraid"
PACKAGE_INSTALL_remove_jetson-tx2-4-gb = " mdraid"
PACKAGE_INSTALL_remove_jetson-tx2 = " mdraid"
PACKAGE_INSTALL_remove_n310-tx2 = " mdraid"
PACKAGE_INSTALL_remove_n510-tx2 = " mdraid"
PACKAGE_INSTALL_remove_orbitty-tx2 = " mdraid"
PACKAGE_INSTALL_remove_spacely-tx2 = " mdraid"
