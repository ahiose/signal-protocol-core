# Signal Protocol Core

**Open-source Java SDK for traffic signal controller communication protocols.**

An extensible, vendor-neutral SDK that implements national standard protocols for communication between traffic signal controllers and upper-level control systems. Built for real-world interoperability across multiple manufacturers.

---

**交通信号控制机通信协议开源 Java SDK。**

一个可扩展的、厂商中立的 SDK，实现交通信号控制机与上位机之间的国家标准通信协议。面向多厂商互联互通的真实场景而构建。

---

## Features / 特性

- **GB/T 20999-2017** — Full binary frame protocol implementation including CRC16, escape encoding, 10 frame types, and all 18 data classes
- **GB/T 20999-2017** — 完整的二进制帧协议实现，包含 CRC16 校验、转义编码、10 种帧类型、18 个数据类
- **Vendor SPI** — Plug-in architecture for manufacturer-specific adaptations via Java `ServiceLoader`. Add a JAR, get vendor support — zero configuration
- **厂商 SPI 扩展** — 基于 Java `ServiceLoader` 的插件式厂商适配架构。引入 JAR 包即自动激活，零配置
- **Per-connection vendor binding** — A single process can manage connections to signal controllers from different vendors simultaneously
- **按连接绑定厂商** — 单进程可同时管理不同厂商信号机的连接
- **Multi-protocol ready** — Top-level `protocol-common` module is protocol-agnostic, designed for future protocols (GA/T 1049, NTCIP, etc.)
- **多协议就绪** — 顶层 `protocol-common` 模块与协议无关，为未来协议扩展预留（GA/T 1049、NTCIP 等）
- **Java 8+** — Maximum compatibility across projects and environments
- **Java 8+** — 最大化项目和环境兼容性

## Project Structure / 工程结构

```
signal-protocol-core/
├── protocol-common/                  Protocol-agnostic abstractions & SPI
│                                     协议无关的公共抽象与 SPI 接口
│
├── protocol-gb20999/                 GB/T 20999-2017 implementation
│   ├── gb20999-core/                 Frame, CRC16, escape, constants, codec
│   ├── gb20999-codec-netty/          Netty ChannelHandler codecs
│   ├── gb20999-client/               High-level client API
│   ├── gb20999-server/               Server API (simulate / test)
│   └── gb20999-spring-boot-starter/  Spring Boot auto-configuration
│
├── vendor-adapters/
│   ├── vendor-keli/                  KeLi (科力) adapter
│   └── vendor-svision/              SVision (圣维信) adapter
│
└── samples/
    ├── sample-gb20999-basic/         Standalone usage example
    └── sample-gb20999-spring-boot/   Spring Boot integration example
```

## Quick Start / 快速开始

### Maven

```xml
<!-- Core protocol (zero external dependencies) -->
<!-- 核心协议（零外部依赖） -->
<dependency>
    <groupId>com.opensignal</groupId>
    <artifactId>gb20999-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Client API with Netty transport -->
<!-- 基于 Netty 传输的客户端 API -->
<dependency>
    <groupId>com.opensignal</groupId>
    <artifactId>gb20999-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Vendor adapter (optional, add as needed) -->
<!-- 厂商适配包（可选，按需引入） -->
<dependency>
    <groupId>com.opensignal</groupId>
    <artifactId>vendor-keli</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Build from Source / 源码构建

```bash
git clone https://github.com/ahiose/signal-protocol-core.git
cd signal-protocol-core
mvn clean compile
```

Requires **JDK 8+** and **Maven 3.6+**.

需要 **JDK 8+** 和 **Maven 3.6+**。

### Encode & Decode a Frame / 编码与解码帧

```java
import com.opensignal.protocol.gb20999.core.frame.*;
import com.opensignal.protocol.gb20999.core.constant.DataClassId;
import java.util.Collections;

// Build a query frame for phase info
// 构建查询相位信息的帧
DataValue dv = DataValue.queryOf(0, DataClassId.PHASE, 1, 0, 0);
Frame frame = Frame.query(0x01, 0x00000001, 0x01, 0x0A,
        Collections.singletonList(dv));

// Encode to bytes (with TCP escape)
// 编码为字节（TCP 模式含转义）
byte[] bytes = FrameEncoder.encode(frame, true);

// Decode back (strip start/end bytes and unescape first)
// 解码还原（先去除首尾字节并反转义）
byte[] body = EscapeCodec.unescape(
        Arrays.copyOfRange(bytes, 1, bytes.length - 1));
Frame decoded = FrameDecoder.decode(body);
```

## Vendor Extension / 厂商扩展

Implement `VendorProfile` to adapt to any signal controller manufacturer:

实现 `VendorProfile` 接口即可适配任意信号机厂商：

```java
public class MyVendorProfile implements VendorProfile {

    @Override public String vendorId() { return "my-vendor"; }
    @Override public String vendorName() { return "My Vendor"; }

    @Override
    public int toDeviceControlMode(int standardMode) {
        // Map standard mode to device-specific code
        // 将标准模式码映射为设备私有码
    }

    @Override
    public int toStandardControlMode(int deviceMode) {
        // Map device code back to standard mode
        // 将设备私有码映射回标准模式码
    }
}
```

Register via SPI — create `META-INF/services/com.opensignal.protocol.common.spi.VendorProfile`:

通过 SPI 注册 — 创建 `META-INF/services/com.opensignal.protocol.common.spi.VendorProfile`：

```
com.example.MyVendorProfile
```

The SDK discovers and activates vendor profiles automatically at runtime.

SDK 在运行时自动发现并激活厂商配置。

## Architecture / 架构

```
┌───────────────────────────────────────────┐
│             User Application              │
├───────────────────────────────────────────┤
│       gb20999-client / server             │  High-level API / 高级 API
├───────────────────────────────────────────┤
│       gb20999-codec-netty                 │  Netty codecs / Netty 编解码
├───────────────────────────────────────────┤
│       gb20999-core                        │  Frame + CRC + Model / 帧+校验+模型
├──────────┬────────────────────────────────┤
│          │  VendorProfile (SPI)           │  Extension point / 扩展点
│          ├────────────────────────────────┤
│          │ ┌──────┐ ┌───────┐ ┌────────┐ │
│          │ │ KeLi │ │SVision│ │  ...   │ │  Vendor adapters / 厂商适配
│          │ └──────┘ └───────┘ └────────┘ │
├──────────┴────────────────────────────────┤
│          protocol-common                  │  Protocol-agnostic / 协议无关层
└───────────────────────────────────────────┘
```

## Roadmap / 路线图

- [x] Project skeleton & build / 工程骨架与构建
- [x] GB/T 20999 frame codec (encode/decode/CRC/escape) / 帧编解码
- [x] Standard constants (18 data classes, frame types, run modes, etc.) / 标准常量
- [x] Vendor SPI & KeLi/SVision adapters / 厂商 SPI 与科力/圣维信适配
- [ ] Netty ChannelHandler codecs / Netty 编解码器
- [ ] High-level client API / 高级客户端 API
- [ ] Server API for testing / 测试用服务端 API
- [ ] Spring Boot Starter / Spring Boot 自动配置
- [ ] GA/T 1049 protocol module / GA/T 1049 协议模块

## License / 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Contributing / 贡献

Contributions are welcome. Please open an issue to discuss before submitting a PR.

欢迎贡献代码。提交 PR 前请先开 Issue 讨论。
