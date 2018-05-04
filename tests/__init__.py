import sys
import os

base_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
tsuro_path = os.path.join(base_path, 'tsuro')
tests_path = os.path.join(base_path, 'tests')

sys.path = [tsuro_path, tests_path] + sys.path
