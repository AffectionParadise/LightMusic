import io
import sys
import json
from curl_cffi import requests

# 【核心修复代码】强制标准输出和标准错误的编码为 UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
# 如果需要处理输入参数中的中文，也需要这一行
sys.stdin = io.TextIOWrapper(sys.stdin.buffer, encoding='utf-8')

def request(url, impersonate='chrome131'):
    try:
        response = requests.get(url=url, impersonate=impersonate, timeout=10)
        return response.text
    except Exception as e:
        return {"code": 500, "msg": f"请求异常: {str(e)}"}

if __name__ == "__main__":
    # 从命令行参数获取 URL
    if len(sys.argv) >= 2:
        url = sys.argv[1]
        impersonate = sys.argv[2] if len(sys.argv) > 2 else 'chrome131'
        print(request(url, impersonate))